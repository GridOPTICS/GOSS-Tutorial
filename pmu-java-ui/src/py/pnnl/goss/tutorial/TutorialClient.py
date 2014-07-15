#!/usr/bin/env python

import matplotlib
matplotlib.use('TkAgg')

import datetime
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg #, NavigationToolbar2TkAgg



from matplotlib.figure import Figure

#import sys
import tkinter as Tk

import stomp
import json
from py4j.java_gateway import JavaGateway
gateway = JavaGateway()    
gateway.launch_gateway
client = gateway.jvm.pnnl.goss.core.client.GossClient()

agg_topic = '/topic/pmu/PMU_1/PMU_2/agg'
username = 'pmu_user'
pw = 'password'
startDateStr = "2014-07-10 00:00:00.000"

#values for the graph
graphTimes = []
phasorValues = []
phasor2Values = []
differences = []
showGraph=False

class StompListener(object):
    def on_error(self, headers, message):
        print('received an error %s' % message)
    def on_message(self, headers, message):
        topic = headers.pop("destination", "")
        if(topic==agg_topic):
            #Parse json message
            jdata = json.loads(message)
            timeStr = jdata['timestamp']
            formatter = gateway.jvm.pnnl.goss.tutorial.datamodel.PMUPhaseAngleDiffData.DATE_FORMAT
            time = formatter.parse(timeStr)
            
            print(' uploading %s' % message)
            #Send to goss using client api
            dataObj = gateway.jvm.pnnl.goss.tutorial.datamodel.PMUPhaseAngleDiffData(time, jdata['phasor1'], jdata['phasor2'], jdata['difference'])
            uploadReq = gateway.jvm.pnnl.goss.core.UploadRequest(dataObj,"Tutorial")
            client.getResponse(uploadReq)
            
            #Update last posted label
            newLabel = str(time)+", "+str(jdata['phasor1'])+", "+str(jdata['phasor1'])+", "+str(jdata['difference'])
            addedTextVar.set(newLabel)
            
            #update graph data
            if showGraph:
                print('update graph')
                timestamp = datetime.datetime.strptime(jdata['timestamp'], '%Y-%m-%d %H:%M:%S.%f')
                graphTimes.append(timestamp)
                phasorValues.append(jdata['phasor1'])
                phasor2Values.append(jdata['phasor2'])
                differences.append(abs(jdata['difference']))
                graphPlotA.plot_date(x=graphTimes, y=differences, xdate=True, ydate=False, linestyle='-', marker='None')
                figure.canvas.draw()
            
        else:
            print('received a message on another topic %s ' % topic)




#set up the window
root = Tk.Tk()
root.wm_title("GOSS tutorial Client")
widthFrame = Tk.Frame(root, width=800, height=1)
widthFrame.pack(fill=Tk.X, expand=True)
heightFrame = Tk.Frame(root, width=1, height=600)
heightFrame.pack(side=Tk.RIGHT, fill=Tk.Y, expand=True)




figure = Figure(figsize=(5,4), dpi=150)
graphPlotA = figure.add_subplot(111)

# this fixes the toolbar x coord 
graphPlotA.fmt_xdata = matplotlib.dates.DateFormatter('%m-%d %H-%M-%S') 
# this rotates the ticklabels to help with overlapping 
figure.autofmt_xdate() 
#figure.title("Phase Angle Difference")


# a tk.DrawingArea
canvas = FigureCanvasTkAgg(figure, master=root)




def _quit():
    root.quit()     # stops mainloop
    root.destroy()  # this is necessary on Windows to prevent
                    # Fatal Python Error: PyEval_RestoreThread: NULL tstate

buttonFrame = Tk.Frame(root )
buttonFrame.pack( side = Tk.TOP)                    

#button = Tk.Button(master=root, text='Quit', command=_quit)
#button.pack(side=Tk.BOTTOM)

conn = None
def _monitor():
    global conn
    if monitorbutton.config('text')[-1] == 'Start Monitoring':
        monitorbutton.config(text='Stop Monitoring')
        # do monitoring stuff
        print('Start Monitoring')
        graphbutton.grid(row=2, column=0)
        
        #start connection
        
        conn = stomp.Connection([('localhost',61613)])
        conn.set_listener('', StompListener())
        conn.start()
        conn.connect('pmu_user', 'password')
        conn.subscribe(destination=agg_topic, id=1, ack='auto')
        
        #conn.subscribe(destination='/topic//pmu/PMU_1', id=1, ack='auto')
        
        addedTextLabel.grid(row=1, column=3)
        addedText.grid(row=1, column=4)
    else:
        monitorbutton.config(text='Start Monitoring')
        #stop the monitoring
        #hide the graph
        graphbutton.grid_forget()
        addedTextLabel.grid_forget()
        addedText.grid_forget()
        canvas._tkcanvas.pack_forget()
        print('Stop Monitoring')    
        if(conn!=None):
            conn.disconnect()
                        

monitorbutton = Tk.Button(master=buttonFrame, text='Start Monitoring', command=_monitor)
monitorbutton.grid(row=1, column=0)

addedTextLabel = Tk.Label(master=buttonFrame, text="Last Entry Added:")
#addedTextLabel.grid(row=1, column=3)

addedTextVar = Tk.StringVar()
addedText = Tk.Label(master=buttonFrame, textvariable=addedTextVar)
#addedText.grid(row=1, column=4)


def _graph():
    #values for the graph
    global graphTimes 
    global phasorValues 
    global phasor2Values 
    global differences 
    global showGraph 
    if graphbutton.config('text')[-1] == 'Show Graph':
        graphbutton.config(text='Hide Graph')
        # start the poll for graph data
        # show the graph
        print('Show Graph')
        canvas.show()
        canvas.get_tk_widget().pack(side=Tk.BOTTOM, fill=Tk.BOTH, expand=1, )
       
        #send request for graph data
        formatter = gateway.jvm.pnnl.goss.tutorial.datamodel.PMUPhaseAngleDiffData.DATE_FORMAT
        startDate = formatter.parse(startDateStr)
        dataRequest = gateway.jvm.pnnl.goss.tutorial.request.TutorialDownloadRequestSync(startDate)
        print('Created Request')
        response = client.getResponse(dataRequest)
        data = response.getData()
        print(data)
        jdata = json.loads(data)
        
        for x in jdata:
            timestamp = datetime.datetime.strptime(x['timestamp'], '%Y-%m-%d %H:%M:%S.%f')
            print(timestamp,' ',x['phasor1'],' ',x['phasor2'],' ',abs(x['difference']))
            graphTimes.append(timestamp)
            phasorValues.append(x['phasor1'])
            phasor2Values.append(x['phasor2'])
            differences.append(abs(x['difference']))
            
        #graphPlotA.plot_date(x=graphTimes, y=phasorValues, xdate=True, ydate=False, linestyle='-', marker='None')
        #graphPlotA.plot_date(x=graphTimes, y=phasor2Values, xdate=True, ydate=False, linestyle='-', marker='None')
        graphPlotA.plot_date(x=graphTimes, y=differences, xdate=True, ydate=False, linestyle='-', marker='None')
       
        showGraph=True
       
    else:
        graphbutton.config(text='Show Graph')
        # stop the poll for graph data
        # hide the graph
        print('Hide Graph')  
        canvas._tkcanvas.pack_forget()
        showGraph=False
        graphTimes = []
        phasorValues = []
        phasor2Values = []
        differences = []
        

graphbutton = Tk.Button(master=buttonFrame, text='Show Graph', command=_graph)

Tk.mainloop()
# If you put root.destroy() here, it will cause an error if
# the window is closed with the window manager.