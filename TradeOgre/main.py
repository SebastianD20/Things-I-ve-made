import trade as t
import time


# Start variables
market = "BTC-USDT"
start_time = time.time()
timer = start_time
time_to_wait = 5
percentage = 1
past_price = float(t.order_book(market, 'buy'))
lowest_btc = 1000000
highest_btc = 0
start_btc_balance = float(t.balance('BTC'))
start_usdt_balance = float(t.balance('USDT'))
start_total_balance = start_usdt_balance + start_btc_balance * float(t.order_book(market, 'buy'))


while True:
    btc_balance = float(t.balance('BTC'))
    usdt_balance = float(t.balance('USDT'))
    current_ = float(t.order_book(market, 'buy'))
    total_balance = usdt_balance + (btc_balance * current_)
    profit = total_balance-start_total_balance
    if profit < (-0.25 * start_total_balance):
        break
    if btc_balance > (total_balance / current_ / 2):
        current_btc = round(float(t.order_book(market, 'buy')), 2)
        if highest_btc < current_btc:
            #print('\n')
            #print(f'New High:{current_btc}, change:{current_btc-highest_btc}')
            highest_btc = current_btc
        if highest_btc > (1+(percentage/100)) * float(t.order_book(market, 'buy')):
            #print(f'Highest: {highest_btc} | Current: {t.order_book(market, 'buy')}')
            price = float(t.order_book(market, 'buy'))
            amount = round(price * usdt_balance * .95, 8)
            if amount > 0.00005:
                try:
                    print('\n')
                    selling_ = t.sell(market, btc_balance, price)
                    if selling_ != True:
                        print(selling_)
                        print('Failed to sell BTC.')
                    #print('\n')
                    else:
                        print(f'Selling: {market}, {btc_balance}, {price}')
                    lowest_btc = 1000000
                    highest_btc = 0
                except Exception as e:
                    print(e)
            else:
                print('Not enough BTC.')
    if usdt_balance > (total_balance / 2):
        current_btc = round(float(t.order_book(market, 'sell')), 2)
        if lowest_btc > current_btc:
            #print('\n')
            #print(f'New Low:{current_btc}, change:{current_btc-lowest_btc}')
            lowest_btc = current_btc
        if lowest_btc * (1+(percentage/100)) < float(t.order_book(market, 'sell')):
            #print(f'Lowest: {lowest_btc} | Current: {t.order_book(market, 'sell')}')
            price = float(t.order_book(market, 'sell'))
            amount = round(usdt_balance/price*.75, 8)
            if amount * price > 2:
                try:
                    print('\n')
                    buying_ = t.buy(market, amount, price)
                    if buying_ != True:
                        print(buying_)
                        print('Failed to buy BTC.')
                    else:
                        print(f'Buying: {market}, {btc_balance}, {price}')
                    #print('\n')
                    lowest_btc = 1000000
                    highest_btc = 0
                except Exception as e:
                    print(e)
            else:
                print('Not enough USDT.')
    for i in range(time_to_wait):
        time.sleep(1)
        print(f'Sleeping for {i}/{time_to_wait} | Balance:{total_balance:.2f}, Profit={profit:.2f}, Losest={lowest_btc:.2f}, Highest={highest_btc:.2f}', end='\r')
    past_price = float(t.order_book(market, 'buy'))
