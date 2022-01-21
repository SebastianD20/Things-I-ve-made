import cv2
import numpy as np
import os
from time import time
from windowcapture import WindowCapture
from vision import findClickPositions


os.chdir(os.path.dirname(os.path.abspath(__file__)))

wincap = WindowCapture('RuneScape')

loop_time = time()
while True:
    screenshot = wincap.get_screenshot()
#    screenshot = np.array(screenshot)
#    screenshot = cv2.cvtColor(screenshot, cv2.COLOR_RGB2BGR)

#    cv2.imshow('Computer Vision', screenshot)
    findClickPositions('capture.jpg', screenshot, 0.5, 'rectangles')

    print('FPS: {}'.format(int(1 / (time() - loop_time))))
    loop_time = time()

    if cv2.waitKey(1) == ord('p'):
        cv2.destroyAllWindows()
        break
