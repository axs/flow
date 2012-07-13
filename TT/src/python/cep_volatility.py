

import sys
import pika
import simplejson as json

# Import all adapters for easier experimentation
from pika.adapters import *

import time,multiprocessing
import numpy as np
import matplotlib
matplotlib.use('WXAgg') # do this before importing pylab

import matplotlib.pyplot as plt


# Major library imports
import random
import wx
from numpy import arange, array, hstack, random


esvolq = multiprocessing.Queue()
clvolq = multiprocessing.Queue()



class test(object):
    pika.log.setup(pika.log.INFO, color=True)

    connection = None
    channel = None

    def __init__ (self,routename,esmq, clmq):
        self.rkey=routename
        self.esmqname=esmq
        self.clmqname=clmq
        self.ES = '00A0FL00ESZ' # 'ES01110600000000NN'
        self.CL = '00A0FL00CLZ'  # 'CL01110500000000NN'
        #self.ES = 'ES01110600000000NN' #
        #self.CL = 'CL01110500000000NN' #


    def on_connected(self,connection):
        global channel
        pika.log.info("demo_receive: Connected to RabbitMQ")
        connection.channel(self.on_channel_open)


    def on_channel_open(self,channel_):
        global channel
        channel = channel_
        #exechnagename='ORDERS'
        exechnagename='CEP'
        pika.log.info("demo_receive: Received our Channel")
        channel.exchange_declare(exchange=exechnagename,type='direct' ,callback=self.on_exchange_declared)
        channel.queue_declare( exclusive=True ,auto_delete=True
                              ,callback=self.on_queue_declared ,queue='')
        channel.queue_bind( exchange=exechnagename,queue='',routing_key=self.rkey,callback=self.on_queue_binded )


    def on_queue_binded(self,frame):
        pika.log.info("demo_receive: Queue Binded")

    def on_exchange_declared (self,frame):
        pika.log.info("demo_receive: Exchange Declared")

    def on_queue_declared(self,frame):
        pika.log.info("demo_receive: Queue Declared")
        channel.basic_consume(self.handle_delivery, queue='')


    def handle_delivery(self,channel, method_frame, header_frame, body):
        jb= json.loads(body)
        #print jb
        if jb["PriceEvent"]["securityID"] == self.ES:
            self.esmqname.put(jb["PriceEvent"]["sdbid"])
        elif jb["PriceEvent"]["securityID"] == self.CL:
            self.clmqname.put(jb["PriceEvent"]["sdbid"])
        channel.basic_ack(delivery_tag=method_frame.delivery_tag)


def run (esmvolq, clmvolq):
    routekey = 'volatility'
    host = '127.0.0.1'
    credentials = pika.PlainCredentials('sdfsd', 'secret')
    parameters = pika.ConnectionParameters(credentials=credentials,host=host,virtual_host='dev')

    z=test(routekey,esmvolq, clmvolq)
    connection = SelectConnection(parameters, z.on_connected)

    try:
        connection.ioloop.start()
    except KeyboardInterrupt:
        connection.close()
        connection.ioloop.start()



if __name__ == '__main__':
    p = multiprocessing.Process(target=run, args=(esvolq,clvolq))
    p.start()



    #graph
    fig = plt.figure()

    MX=500
    hax = fig.add_subplot(211,axisbg='darkslategray', title='es vol')
    hax.grid(True)
    hydata = np.arange(0)
    hxdata = np.arange( len(hydata) )
    hline, = hax.plot(hxdata, hydata, color='y')

    cax = fig.add_subplot(212,axisbg='darkslategray', title='cl vol')
    cax.grid(True)
    cydata = np.arange(0)
    cxdata = np.arange( len(cydata) )
    cline, = cax.plot(cxdata, cydata, color='y')

    def update_line(event):
        global hydata ,cydata
        redraw = False
        if not esvolq.empty():
            hydata = np.hstack((hydata[-MX+1:], [ esvolq.get_nowait() ]))
            hax.set_ylim(min(hydata),max(hydata))
            hax.set_xlim(0,len(hydata))
            hline.set_ydata( hydata )
            hline.set_xdata(np.arange( len(hydata)) )
            redraw=True
        if not clvolq.empty():
            cydata = np.hstack((cydata[-MX+1:], [ clvolq.get_nowait() ]))
            cax.set_ylim(min(cydata),max(cydata))
            cax.set_xlim(0,len(cydata))
            cline.set_ydata( cydata )
            cline.set_xdata(np.arange( len(cydata)) )
            redraw=True
        if redraw:
            fig.canvas.draw()


    import wx
    id = wx.NewId()
    actor = fig.canvas.manager.frame
    timer = wx.Timer(actor, id=id)
    timer.Start(1000)
    wx.EVT_TIMER(actor, id, update_line)
    plt.show()


    if p.is_alive():
        #p.join()
        print "killing p"
        p.terminate()



