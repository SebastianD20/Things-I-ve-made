import requests
import time

api_url = "https://tradeogre.com/api/v1"
key = "ENTER KEY HERE"
secret = "ENTER SECRET HERE"

what_coin = 'BTC'
secondary_coin = 'USDT'
what_market = 'BTC-USDT'

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
    btc_balance = float(balance(secondary_coin))
    iron_balance = float(balance(what_coin))
    orders = check_my_orders(coin, 'sell')
    if coin == what_coin and orders != False:
        for order in orders:
            iron_balance += order['quantity']
        return iron_balance
    orders = check_my_orders(coin, 'buy')
    if coin == secondary_coin and orders != False:
        for order in orders:
            btc_balance += (order['quantity'] * order['price'])
        return btc_balance

starting_btc = float(balance(secondary_coin))
price = order_book(what_market, 'buy')
btc_value = float(starting_btc) / float(price)
starting_iron = float(balance(what_coin)) + float(btc_value)
start_time = int(time.time())

while True:
    btc_balance = float(balance(secondary_coin))
    iron_balance = float(balance(what_coin))

    if iron_balance > 0:
        current_order_book_sell_price = order_book(what_market, 'sell')
        my_current_orders_open = check_my_orders(what_market, 'sell')
        if my_current_orders_open != False:
            for order in my_current_orders_open:
                if str(order['price']) != str(current_order_book_sell_price):
                    #print(str(order['price']) == str(current_order_book_sell_price))
                    if cancel(order['uuid']):
                        h, m, s = runtime()
                        print('Runtime: {}:{}:{}, Canceled Order:{}'.format(h, m, s, order))
                        iron_balance = float(balance(what_coin))
                        #h, m, s = runtime()
                        #print('Runtime: {}:{}:{}, Starting {}:{}, Current {}:{}'.format(h, m, s, what_coin, starting_iron, what_coin, iron_balance))
                    else:
                        print('Failed to cancel order.')
        iron_orders = check_my_orders(what_market, 'sell')
        if iron_orders == False or iron_orders == []:
            iron_balance = float(balance(what_coin))
            price = ('{:.8f}').format(float(float(current_order_book_sell_price) - 0.00000001))
            amount = (float(iron_balance)/2)
            amount = ('{:.8f}'.format(float(amount)))
            value = float(amount) * float(price)
            if float(value) > 1: #0.00005:
                success = sell(what_market, amount, price)
                if success == True:
                    h, m, s = runtime()
                    print('Runtime: {}:{}:{}, Opened sell order. Market:{}, Amount:{}, Price:{}'.format(h, m, s, what_market, amount, price))
                else:
                    h, m, s = runtime()
                    print(success)
                    print('Runtime: {}:{}:{}, Failed to open sell order.'.format(h, m, s))
    btc_value = price

    if float(btc_balance) > 0:
        current_order_book_buy_price = order_book(what_market, 'buy')
        price = ('{:.8f}').format(float(float(current_order_book_buy_price) + 0.00000000))
        my_current_orders_open = check_my_orders(what_market, 'buy')
        if my_current_orders_open != False:
            for order in my_current_orders_open:
                if order['price'] != current_order_book_buy_price:
                    if cancel(order['uuid']):
                        h, m, s = runtime()
                        print('Runtime: {}:{}:{}, Canceled Order:{}'.format(h, m, s, order))
                        btc_balance = float(balance(secondary_coin))
                        #h, m, s = runtime()
                        #print('Runtime: {}:{}:{}, Starting BTC:{}, Current BTC:{}'.format(h, m, s, starting_btc, btc_balance))
                    else:
                        h, m, s = runtime()
                        print('Runtime: {}:{}:{}, Failed to cancel order.'.format(h, m, s))
        btc_orders = check_my_orders(what_market, 'buy')
        if btc_orders == False or btc_orders == []:
            btc_balance = float(balance(secondary_coin))
            price = ('{:.8f}').format(float(float(current_order_book_buy_price) + 0.00000000))
            amount = float(btc_balance)/2/float(price)
            amount = ('{:.8f}'.format(float(amount)))
            value = float(amount) * float(price)
            if float(value) >= 1: # 0.00005:
                success = buy(what_market, amount, price)
                if success == True:
                    h, m, s = runtime()
                    print('Runtime: {}:{}:{}, Opened buy order. Market:{}, Amount:{}, Price:{}'.format(h, m, s, what_market, amount, price))
                else:
                    print(success)
                    h, m, s = runtime()
                    print('Runtime: {}:{}:{}, Failed to open buy order.'.format(h, m, s))
            else:
                #print('Not enough BTC:{:.8f}'.format(value))
                pass
    iron_value = price

    try:
        total_iron_balance = (f'{total_balance(what_coin):.8f}')
        total_btc_balance = ('{:.8f}'.format(total_balance(secondary_coin)))
        btc_in_iron = ('{:.8f}'.format(float(total_btc_balance) / float(price) + float(total_iron_balance)))
        total_profit = ('{:.8f}'.format(float(btc_in_iron) - float(starting_iron)))
        total_profit = (f'{float(total_profit) * float(price):.8f}')
        btc_in_iron = (f'{float(btc_in_iron) * float(price):.8f}')
    except Exception as e:
        print(f'Error:{e}')    
    sleep_time = 3
    for i in range(sleep_time):
        h, m, s = runtime()
        print('Runtime: {}:{}:{}, Sleeping {}/{}, {} Balance:{}, {} Balance:{}, total value:{}, total profit({}):{}            '.format(h, m, s, i, sleep_time, what_coin, total_iron_balance, secondary_coin, total_btc_balance, btc_in_iron, what_coin, total_profit), end='\r')
        time.sleep(1)