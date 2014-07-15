#!/usr/bin/env python

import matplotlib
from matplotlib.pyplot import plot
matplotlib.use('TkAgg')

import datetime
from numpy import arange, sin, pi
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
            
            
        else:
            print('received a message on another topic %s ' % topic)




#set up the window
root = Tk.Tk()
root.wm_title("GOSS tutorial Client")
widthFrame = Tk.Frame(root, width=800, height=1)
widthFrame.pack(fill=Tk.X, expand=True)
heightFrame = Tk.Frame(root, width=1, height=550)
heightFrame.pack(side=Tk.RIGHT, fill=Tk.Y, expand=True)




figure = Figure(figsize=(5,4), dpi=150)
graphPlotA = figure.add_subplot(111)

# only write ticklabels on the decades 
#def fmtticks(x, pos=None): 
    #dt = x #matplotlib.dates.num2date(x) 
    #if dt.year%10: return '' 
    #return dt.strftime('%Y')
#    print(x)
#    return x 

# this fixes the toolbar x coord 
graphPlotA.fmt_xdata = matplotlib.dates.DateFormatter('%y-%m-%d %H-%M-%S') 

# this rotates the ticklabels to help with overlapping 
figure.autofmt_xdate() 

#graphPlotB = figure.add_subplot(111)
#t = arange(0.0,3.0,0.01)
#s = sin(2*pi*t)

#a.plot(t,s)


# a tk.DrawingArea
canvas = FigureCanvasTkAgg(figure, master=root)


#toolbar = NavigationToolbar2TkAgg( canvas, root )
#toolbar.update()

#def on_key_event(event):
#    print('you pressed %s'%event.key)
#    key_press_handler(event, canvas, toolbar)

#canvas.mpl_connect('key_press_event', on_key_event)



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
        
    else:
        monitorbutton.config(text='Start Monitoring')
        #stop the monitoring
        #hide the graph
        graphbutton.grid_forget();
        canvas._tkcanvas.pack_forget()
        print('Stop Monitoring')    
        if(conn!=None):
            conn.disconnect()
                        

monitorbutton = Tk.Button(master=buttonFrame, text='Start Monitoring', command=_monitor)
monitorbutton.grid(row=1, column=0)

addedTextLabel = Tk.Label(master=buttonFrame, text="Last Entry Added:")
addedTextLabel.grid(row=1, column=3)

addedTextVar = Tk.StringVar()
addedText = Tk.Label(master=buttonFrame, textvariable=addedTextVar)
addedText.grid(row=1, column=4)


def _graph():
    if graphbutton.config('text')[-1] == 'Show Graph':
        graphbutton.config(text='Hide Graph')
        # start the poll for graph data
        # show the graph
        print('Show Graph')
        canvas.show()
        canvas.get_tk_widget().pack(side=Tk.BOTTOM, fill=Tk.BOTH, expand=1, )
       
        #send request for graph data
        formatter = gateway.jvm.pnnl.goss.tutorial.datamodel.PMUPhaseAngleDiffData.DATE_FORMAT
        startDate = formatter.parse("2014-07-01 00:00:00.000")
        dataRequest = gateway.jvm.pnnl.goss.tutorial.request.TutorialDownloadRequestSync(startDate)
        print('Created Request')
        response = client.getResponse(dataRequest)
        data = response.getData()
        print(data)
        jdata = json.loads(data)
        times = []
        phasorValues = []
        phasor2Values = []
        for x in jdata:
            print(x['timestamp'])
            timestamp = datetime.datetime.strptime(x['timestamp'], '%Y-%m-%d %H:%M:%S.%f')
            #entry = [timestamp, x['phasor1']]
            #plotdata.append(entry)
            
            #timestamp = formatter.parse(x['timestamp']).getTime()
            #print(timestamp)
            times.append(timestamp)
            phasorValues.append(x['phasor1'])
            phasor2Values.append(x['phasor2'])
            
        #dates_float = matplotlib.dates.date2num(times)    
        graphPlotA.plot_date(x=times, y=phasorValues, xdate=True, ydate=False, linestyle='-')
        graphPlotA.plot_date(x=times, y=phasor2Values, xdate=True, ydate=False, linestyle='-')
       
    else:
        graphbutton.config(text='Show Graph')
        # stop the poll for graph data
        # hide the graph
        print('Hide Graph')  
        canvas._tkcanvas.pack_forget()
        

graphbutton = Tk.Button(master=buttonFrame, text='Show Graph', command=_graph)

Tk.mainloop()
# If you put root.destroy() here, it will cause an error if
# the window is closed with the window manager.