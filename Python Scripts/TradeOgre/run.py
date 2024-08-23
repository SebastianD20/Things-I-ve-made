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

def runtime():
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

starting_btc = btc_balance = float(balance('BTC'))
starting_iron = float(balance('IRON'))
start_time = int(time.time())

while True:
    btc_balance = float(balance('BTC'))
    iron_balance = float(balance('IRON'))

    if iron_balance > 0:
        current_order_book_sell_price = order_book('IRON-BTC', 'sell')
        my_current_orders_open = check_my_orders('IRON-BTC', 'sell')
        if my_current_orders_open != False:
            for order in my_current_orders_open:
                if str(order['price']) != str(current_order_book_sell_price):
                    #print(str(order['price']) == str(current_order_book_sell_price))
                    if cancel(order['uuid']):
                        h, m, s = runtime()
                        print('Runtime: {}:{}:{}, Canceled Order:{}'.format(h, m, s, order))
                        iron_balance = float(balance('IRON'))
                        h, m, s = runtime()
                        print('Runtime: {}:{}:{}, Starting IRON:{}, Current IRON:{}'.format(h, m, s, starting_iron, iron_balance))
                    else:
                        print('Failed to cancel order.')
        iron_orders = check_my_orders('IRON-BTC', 'sell')
        if iron_orders == False or iron_orders == []:
            print
            price = ('{:.8f}').format(float(float(current_order_book_sell_price) - 0.00000001))
            amount = (float(iron_balance)/2)
            amount = ('{:.8f}'.format(float(amount)))
            if float(amount) > 0.00005:
                success = sell('IRON-BTC', amount, price)
                if success == True:
                    h, m, s = runtime()
                    print('Runtime: {}:{}:{}, Opened sell order. Market:{}, Amount:{}, Price:{}'.format(h, m, s, 'IRON-BTC', (float(iron_balance)/2), price))
                else:
                    h, m, s = runtime()
                    print(success)
                    print('Runtime: {}:{}:{}, Failed to open sell order.'.format(h, m, s))


    if float(btc_balance) > 0:
        current_order_book_buy_price = order_book('IRON-BTC', 'buy')
        price = ('{:.8f}').format(float(float(current_order_book_buy_price) + 0.00000001))
        my_current_orders_open = check_my_orders('IRON-BTC', 'buy')
        if my_current_orders_open != False:
            for order in my_current_orders_open:
                if order['price'] != current_order_book_buy_price:
                    if cancel(order['uuid']):
                        h, m, s = runtime()
                        print('Runtime: {}:{}:{}, Canceled Order:{}'.format(h, m, s, order))
                        btc_balance = float(balance('BTC'))
                        h, m, s = runtime()
                        print('Runtime: {}:{}:{}, Starting BTC:{}, Current BTC:{}'.format(h, m, s, starting_btc, btc_balance))
                    else:
                        h, m, s = runtime()
                        print('Runtime: {}:{}:{}, Failed to cancel order.'.format(h, m, s))
        btc_orders = check_my_orders('IRON-BTC', 'buy')
        if btc_orders == False or btc_orders == []:
            price = ('{:.8f}').format(float(float(current_order_book_buy_price) + 0.00000001))
            amount = float(btc_balance)/2/float(price)
            amount = ('{:.8f}'.format(float(amount)))
            if float(amount) >= 0.00005:
                success = buy('IRON-BTC', amount, price)
                if success == True:
                    h, m, s = runtime()
                    print('Runtime: {}:{}:{}, Opened buy order. Market:{}, Amount:{}, Price:{}'.format(h, m, s, 'IRON-BTC', amount, price))
                else:
                    print(success)
                    h, m, s = runtime()
                    print('Runtime: {}:{}:{}, Failed to open buy order.'.format(h, m, s))
            else:
                print('Not enough BTC:{}'.format(amount))
    iron_value = price


    total_iron_balance = total_balance('IRON')
    total_btc_balance = total_balance('BTC')
    btc_in_iron = '{:.8f}'.format(float(total_btc_balance) / float(price) + float(total_iron_balance))
    for i in range(10):
        h, m, s = runtime()
        print('Runtime: {}:{}:{}, Sleeping {}/10, IRON Balance:{}, BTC Balance:{}, total value:{}            '.format(h, m, s, i, total_iron_balance, total_btc_balance, btc_in_iron), end='\r')
        time.sleep(1)