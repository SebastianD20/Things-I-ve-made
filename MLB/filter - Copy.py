team1, team2 = input('Team1,Team2:').split(',')

#team1 = 'Yankees'
#team2 = 'Guardians'

team1winstogether = 0
team2winstogether = 0
team1totalscore = 0
team2totalscore = 0
team1homewins = 0
team2homewins = 0

def findteams(line_):
    away_, score_away, home_, score_home = line_.split('\t')
    return

with open('C:\\Users\\PH\\Desktop\\MLB\\mlb2024.txt') as f:
    for line in f:
        away_, score_away, home_, score_home = line.split('\t')
        if score_away != '':
            if team1 in home_ and team2 in away_:
                if score_away > score_home:
                    team2winstogether += 1
                if score_home > score_away:
                    team1winstogether += 1
                    team1homewins += 1
                team1totalscore += int(score_home)
                team2totalscore += int(score_away)

            elif team2 in home_ and team1 in away_:
                if score_away > score_home:
                    team1winstogether += 1
                if score_home > score_away:
                    team2winstogether += 1
                    team2homewins += 1
                team1totalscore += int(score_home)
                team2totalscore += int(score_away)
            
            elif team2 in home_ and team1 not in away_:
                #if score_away > score_home:
                    #team1winstogether += 1
                if score_home > score_away:
                    team2winstogether += 1
                    team2homewins += 1
                #team1totalscore += int(score_home)
                team2totalscore += int(score_away)
   

            elif team2 not in home_ and team1 in away_:
                if score_away > score_home:
                    team1winstogether += 1
                #if score_home > score_away:
                #    team2winstogether += 1
                #    team2homewins += 1
                team1totalscore += int(score_home)
                #team2totalscore += int(score_away)

            if team1 not in home_ and team2 in away_:
                if score_away > score_home:
                    team2winstogether += 1
                #if score_home > score_away:
                #    team1winstogether += 1
                #    team1homewins += 1
                #team1totalscore += int(score_home)
                team2totalscore += int(score_away)

            if team1 in home_ and team2 not in away_:
                #if score_away > score_home:
                #    team2winstogether += 1
                if score_home > score_away:
                    team1winstogether += 1
                    team1homewins += 1
                team1totalscore += int(score_home)
                #team2totalscore += int(score_away)



    print(f'{team1} Wins:{team1winstogether} Home wins:{team1homewins} Total score:{team1totalscore}')
    print(f'{team2} Wins:{team2winstogether} Home wins:{team2homewins} Total score:{team2totalscore}')
