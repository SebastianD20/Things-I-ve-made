import numpy as np
from PIL import ImageGrab
import cv2
import win32gui, win32ui, win32con

screen_w = 1920
screen_h = 1080
window_name = 'PhantomGalaxies  '


def get_screenshot(self):
    wDC = win32gui.GetWindowDC(self.hwnd)
    dcObj = win32ui.CreateDCFromHandle(wDC)
    cDC = dcObj.CreateCompatibleDC()
    dataBitMap = win32ui.CreateBitmap()
    dataBitMap.CreateCompatibleBitmap(dcObj, self.w, self.h)
    cDC.SelectObject(dataBitMap)
    cDC.BitBlt((0, 0), (self.w, self.h), dcObj, (self.cropped_x, self.cropped_y), win32con.SRCCOPY)

    signedInitsArray = dataBitMap.GetBitmapBits(True)
    img = np.fromstring(signedInitsArray, dtype='uint8')
    img.shape = (self.h, self.w, 4)

    # Free Resources
    dcObj.DeleteDC()
    cDC.DeleteDC()
    win32gui.ReleaseDC(self.hwnd, wDC)
    win32gui.DeleteObject(dataBitMap.GetHandle())

    img = img[..., :3]
    img = np.ascontiguousarray(img)

    return img


while True:
    hwnd = win32gui.FindWindow(None, window_name)
    if not hwnd:
        raise Exception('Window not found: {}'.format(window_name))
#    rgb = ImageGrab.grab(bbox=(0, 0, screen_w, screen_h)) #x1, y1, x2, y2
#    rgb = np.array(rgb)
#    cv2.imshow('window_frame', rgb)
    get_screenshot()

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break