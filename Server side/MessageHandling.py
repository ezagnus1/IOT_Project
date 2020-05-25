import socket
import time
import base64
import io
from PIL import Image
from PIL import ImageFile
import cv2
import imutils
import argparse
from skimage.measure import compare_ssim
ImageFile.LOAD_TRUNCATED_IMAGES = True

listensocket = socket.socket()
listensocket2 = socket.socket()
PORT = 7800
PORT2 = 7801
maxConnection = 999
IP = socket.gethostname()

listensocket.bind(('',PORT))
listensocket2.bind(('',PORT2))
listensocket.listen(maxConnection)
listensocket2.listen(maxConnection)
print("Server started at " + IP + "on port"+ str(PORT))

(clientsocket,address) = listensocket.accept()
(clientsocket2,address) = listensocket2.accept()
print("new connection made")

message_temp = clientsocket.recv(1024).decode()
temp_split = []
temp_split = message_temp.split(" ")
message = temp_split[1]

temp = "US"
while (True):
    temp = ""
    temp = clientsocket.recv(1024).decode()
    if(len(temp)== 0):break;
    message += temp

imgdata = base64.b64decode(message)
image = Image.open(io.BytesIO(imgdata))
clientsocket.close()
listensocket.close()
# IMAGE COMPARISON WILL BE HAPPENING HERE !!!!!!!

# construct the argument parse and parse the arguments
image.save("phone_photo.png")
path = temp_split[0]
path2 = 'phone_photo.png'
# Using cv2.imread() method
imageA = cv2.imread(path)
imageB = cv2.imread(path2)
scale_percent = 50
#calculate the 50 percent of original dimensions
width = int(imageA.shape[1] * scale_percent / 100)
height = int(imageA.shape[0] * scale_percent / 100)
#dsize
dsize = (width, height)
# resize image
imageB = cv2.resize(imageB, dsize)
imageA = cv2.resize(imageA, dsize)
# convert the images to grayscale
grayA = cv2.cvtColor(imageA, cv2.COLOR_BGR2GRAY)
grayB = cv2.cvtColor(imageB, cv2.COLOR_BGR2GRAY)
# compute the Structural Similarity Index (SSIM) between the two
# images, ensuring that the difference image is returned
(score, diff) = compare_ssim(grayA, grayB, full=True)
if (score>0.5):
    clientsocket2.send('true'.encode())
    print("Message sent")
    clientsocket2.close()
    listensocket2.close()
else:
    clientsocket2.send('false'.encode())
    print("Message sent")
    clientsocket2.close()
    listensocket2.close()



