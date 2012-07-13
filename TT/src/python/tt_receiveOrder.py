

import sys
import pika
import simplejson as json

# Import all adapters for easier experimentation
from pika.adapters import *


class test(object):
    pika.log.setup(pika.log.INFO, color=True)

    connection = None
    channel = None

    def __init__ (self,routename, exc, exctype):
        self.rkey=routename
        self.exchange = exc
        self.exchangetype = exctype

    def on_connected(self,connection):
        global channel
        pika.log.info("demo_receive: Connected to RabbitMQ")
        connection.channel(self.on_channel_open)


    def on_channel_open(self,channel_):
        global channel
        channel = channel_
        #exechnagename='ORDERS'
        #exechnagename='EXECUTIONS'
        #exechnagename='TRADES'
        pika.log.info("demo_receive: Received our Channel")
        channel.exchange_declare(exchange=self.exchange,type=self.exchangetype ,callback=self.on_exchange_declared)
        channel.queue_declare( exclusive=True ,auto_delete=True
                              ,callback=self.on_queue_declared ,queue='')
        channel.queue_bind( exchange=self.exchange,queue='',routing_key=self.rkey,callback=self.on_queue_binded )


    def on_queue_binded(self,frame):
        pika.log.info("demo_receive: Queue Binded")

    def on_exchange_declared (self,frame):
        pika.log.info("demo_receive: Exchange Declared")

    def on_queue_declared(self,frame):
        pika.log.info("demo_receive: Queue Declared")
        channel.basic_consume(self.handle_delivery, queue='')


    def handle_delivery(self,channel, method_frame, header_frame, body):
        #pika.log.info("Basic.Deliver %s delivery-tag %i: %s",
        #              header_frame.content_type,
        #              method_frame.delivery_tag,
        #              body)
        jb= json.loads(body)
        print jb
        #channel.basic_ack(delivery_tag=method_frame.delivery_tag)

if __name__ == '__main__':
    routekey = (len(sys.argv) > 1) and sys.argv[1] or 'fred'
    host = '127.0.0.1'
    credentials = pika.PlainCredentials('sdfsd', 'secret')
    parameters = pika.ConnectionParameters(credentials=credentials,host=host,virtual_host='dev')

    #exch='ORDERS'
    #exch='EXECUTIONS'
    #etyp='direct'

    exch='TRADES'
    etyp='topic'
    routekey='ES.*'
    z=test(routekey,exch,etyp)

    connection = SelectConnection(parameters, z.on_connected)
    try:
        connection.ioloop.start()
    except KeyboardInterrupt:
        connection.close()
        connection.ioloop.start()

