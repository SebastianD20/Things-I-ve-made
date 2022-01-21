import win32gui
import os
import time


def winEnumHandler(hwnd, ctx):
    if win32gui.IsWindowVisible(hwnd):
        print(win32gui.GetWindowText(hwnd))
        proc = win32gui.GetWindowText(hwnd)
        proc2 = proc.split('\n')
        for process in proc2:
            list1.append(process)


def openPhantom():
    os.chdir(r'C:/Users/PH/Desktop/')
    os.startfile("Phantom Galaxies Launcher")


list1 = []
list2 = []
win32gui.EnumWindows(winEnumHandler, None)
print(list1)

