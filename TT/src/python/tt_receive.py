

import sys
import pika
import simplejson as json

# Import all adapters for easier experimentation
from pika.adapters import *

pika.log.setup(pika.log.INFO, color=True)

connection = None
channel = None


def on_connected(connection):
    global channel
    pika.log.info("demo_receive: Connected to RabbitMQ")
    connection.channel(on_channel_open)


def on_channel_open(channel_):
    global channel
    channel = channel_
    pika.log.info("demo_receive: Received our Channel")
    exname="QUOTES"
    channel.exchange_declare(exchange=exname, type='topic' ,callback=on_exchange_declared)
    channel.queue_declare( exclusive=True ,auto_delete=True
                          ,callback=on_queue_declared ,queue='')
    channel.queue_bind( exchange=exname ,queue='',routing_key="CL.*",callback=on_queue_binded )


def on_queue_binded(frame):
    pika.log.info("demo_receive: Queue Binded")

def on_exchange_declared (frame):
    pika.log.info("demo_receive: Exchange Declared")

def on_queue_declared(frame):
    pika.log.info("demo_receive: Queue Declared")
    channel.basic_consume(handle_delivery, queue='')


def handle_delivery(channel, method_frame, header_frame, body):
    #pika.log.info("Basic.Deliver %s delivery-tag %i: %s",
    #              header_frame.content_type,
    #              method_frame.delivery_tag,
    #              body)
    jb= json.loads(body)
    print jb
    #channel.basic_ack(delivery_tag=method_frame.delivery_tag)

if __name__ == '__main__':
    host = (len(sys.argv) > 1) and sys.argv[1] or '127.0.0.1'
    credentials = pika.PlainCredentials('sdfsd', 'secret')
    parameters = pika.ConnectionParameters(credentials=credentials,host=host,virtual_host='dev')
    connection = SelectConnection(parameters, on_connected)
    try:
        connection.ioloop.start()
    except KeyboardInterrupt:
        connection.close()
        connection.ioloop.start()

