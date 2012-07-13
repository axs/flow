




myfile='c:/tmp/7NA.20110402.bin'

f = open(myfile, "rb")
try:
    byte = f.read(1)
    while byte != "":
        byte = f.read(1)
        #protocl stuff
finally:
    f.close()

