import requests
import time

api_url = "https://tradeogre.com/api/v1"
key = "ENTER KEY HERE"
secret = "ENTER SECRET HERE"

def order_book(market, order):
    # "IRON-BTC"
    markets = "/orders/{}".format(market)
    responce = requests.get(api_url+markets)
    responce_server = responce.json()
    # "buy" "sell"
    order_book = responce_server[order]
    price = list(order_book)[0]
    amount = order_book[price]
    return price

def balance(coin):
    responce_server = requests.get(api_url + '/account/balances', auth=(key, secret)).json()
    return responce_server['balances'][coin]

def my_orders():
    responce_server = requests.post(api_url + '/account/orders', auth=(key, secret)).json()
    return responce_server

def check_my_orders(market, order_type):
    open_orders = my_orders()
    if open_orders == []:
        return False
    elif open_orders != []:
        orders_return = []
        for order in open_orders:
            if order['market'] == market and order['type'] == order_type:
                orders_return.append(order)
        return orders_return
    
def cancel(uuid):
    data = {"uuid": uuid}
    response = requests.post(api_url + '/order/cancel', data=data, auth=(key, secret)).json()
    if response['success'] == True:
        return True
    else:
        return False

def sell(market, qty, price):
    data = {"market": market, "quantity": qty, "price": price}
    response = requests.post(api_url + '/order/sell', data=data, auth=(key, secret)).json()
    if response['success'] == True:
        return True
    else:
        return response

def buy(market, qty, price):
    data = {"market": market, "quantity": qty, "price": price}
    response = requests.post(api_url + '/order/buy', data=data, auth=(key, secret)).json()
    if response['success'] == True:
        return True
    else:
        return response

def runtime(start_time):
    run_time = time.time() - start_time
    h, m, s = 0, 0, 0
    if run_time >= 3600:
        h = int(run_time / 3600)
        run_time = (run_time - (h * 3600))
    if run_time >= 60:
        m = int(run_time / 60)
        run_time = (run_time - (m * 60))
    s = int(run_time)
    return h, m, s

def total_balance(coin):
    btc_balance = float(balance('BTC'))
    iron_balance = float(balance('IRON'))
    if coin == 'IRON':
        for order in check_my_orders(coin, 'sell'):
            iron_balance += order['quantity']
        return iron_balance
    if coin == 'BTC':
        for order in check_my_orders(coin, 'buy'):
            btc_balance += (order['quantity'] * order['price'])
        return btc_balance