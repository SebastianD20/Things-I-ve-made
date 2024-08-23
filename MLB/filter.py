team1, team2 = input('Team1,Team2:').split(',')

#team1 = 'Milwaukee Brewers'
#team2 = 'Chicago Cubs'

team1winstogether = 0
team2winstogether = 0
team1totalscore = 0
team2totalscore = 0
team1homewins = 0
team2homewins = 0

def findteams(line_):
    team1_, temp2, temp3 = line.split('(')
    score1_, team2_ = temp2.split(')')
    score2_, rem = temp3.split(')')
    return team1_, score1_, team2_, score2_

with open('C:\\Users\\PH\\Desktop\\MLB\\raw.txt') as f:
    for line in f:
        if team1 in line and team2 in line:
            team1_, score1_, team2_, score2_ = findteams(line)
            print(f'Home team:{team2_} Score:{score2_} | {team1_} {score1_}')
            
            

            if team1 in team1_:
                if int(score1_) > int(score2_):
                    team1winstogether += 1
                    team1totalscore += int(score1_)
                    team2totalscore += int(score2_)
                else:
                    team2winstogether += 1
                    team2totalscore += int(score2_)
                    team1totalscore += int(score1_)
            else:
                if int(score1_) > int(score2_):
                    team2winstogether += 1
                    team1totalscore += int(score2_)
                    team2totalscore += int(score1_)
                else:
                    team1winstogether += 1
                    team1totalscore += int(score2_)
                    team2totalscore += int(score1_)
                

        elif team1 in line and team2 not in line:
            team1_, score1_, team2_, score2_ = findteams(line)
            #print()
        elif team1 not in line and team2 in line:
            team1_, score1_, team2_, score2_ = findteams(line)
            #print()
    print(f'{team1} wins: {team1winstogether} | {team2} wins: {team2winstogether}')
    print(f'{team1} total score: {team1totalscore} | {team2} total score: {team2totalscore}')
