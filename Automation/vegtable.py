import time
import keyboard
import ait
import pyautogui
import random
import datetime

def rand(high):
    random1 = random.randrange(0, high)
    #print(random1)
    return random1

def closeBank():
    click2(1295+rand(5),233+rand(5))
    return

def openBank():
    click1('bank.png',1206+rand(5),398+rand(5))
    time.sleep(3+(rand(200)/100))
    return

def depositAll():
    click2(1124+rand(5),589+rand(5))
    time.sleep(1+(rand(100)/100))
    return

def clickStall():
    click1('steal.png',388+rand(5),756+rand(5))
    time.sleep(4+(rand(200)/100))
    return

def clickStall2():
    click1('steal.png',928+rand(5),553+rand(5))
    time.sleep(1.5+(rand(100)/100))
    return

def click1(img, x, y):
    ait.move(x,y)
    while True:
        time.sleep(.5)
        try:
            pyautogui.locateOnScreen(f"C:/Users/PH/Desktop/runescape/{img}", confidence=0.80)
            break
        except:
            pass
        
    ait.click(x, y)
    return

def click2(x,y):
    ait.move(x,y)
    time.sleep(.02)
    ait.click(x, y)

print(f'Start Time:{datetime.datetime.now()}')

while keyboard.is_pressed('q') == False:
    time.sleep(1)
    #print(pyautogui.position())

    clickStall()

    try:
        while pyautogui.locateOnScreen("C:/Users/PH/Desktop/runescape/emptySlot.png", confidence=0.80) != None:
            time.sleep(1)
            try:
                while pyautogui.locateOnScreen("C:/Users/PH/Desktop/runescape/attacked.png", confidence=0.80) != None:
                    time.sleep(1)
            except:
                pass
            clickStall2()
    except:
        pass

    openBank()
    depositAll()
    closeBank()
    if pyautogui.locateOnScreen("C:/Users/PH/Desktop/runescape/minimap.png", confidence=0.80) == None:
        print(f'End Time:{datetime.datetime.now()}')
        break

print('Break')

# 10pm 500m
# 9am 560m