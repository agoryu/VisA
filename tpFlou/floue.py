import matplotlib.pyplot as plt

#Calcul des fonction basse, moyenne et eleve
def CalcTempB(i):
    if(i < 10):
        return 1
    elif(i<20):
        return 1 - ((1/10) * (i-10))
    else:
        return 0

def CalcTempM(i):
    if(i < 10):
        return 0
    elif(i<20):
        return ((1/10) * (i-10))
    elif(i<30):
        return 1 - ((1/10) * (i-20))
    else:
        return 0

def CalcTempE(i):
    if(i < 20):
        return 0
    elif(i<30):
        return ((1/10) * (i-20))
    else:
        return 1

#Calcul des operateurs de la logique flou
def Opmin(tab1, tab2):
    size = range(0, len(tab1))
    result = []
    for i in size:
        step = [tab1[i], tab2[i]]
        result.append(min(step))

    return result

def Opmax(tab1, tab2):
    size = range(0, len(tab1))
    result = []
    for i in size:
        step = [tab1[i], tab2[i]]
        result.append(max(step))

    return result

temp = range(0, 40)
temperatureB = []
temperatureM = []
temperatureE = []

for i in temp:
    temperatureB.append(CalcTempB(i))
    temperatureM.append(CalcTempM(i))
    temperatureE.append(CalcTempE(i))

#Exo 1
"""
print("degre d'appartenance pour la temperature 16 :")
print("basse = " + repr(temperatureB[16]))
print("moyen = " + repr(temperatureM[16]))
print("eleve = " + repr(temperatureE[16]))

plt.title("Partition floue")
plt.plot(temp, temperatureB)
plt.plot(temp, temperatureM)
plt.plot(temp, temperatureE)
plt.xlabel('Temperature')
plt.show()
"""

#Exo 2
"""
minTemp = Opmin(temperatureB, temperatureM)
maxTemp = Opmax(temperatureM, temperatureE)

plt.title("Partition floue")
plt.plot(temp, minTemp)
plt.plot(temp, maxTemp)
plt.xlabel('Temperature')
plt.show()
"""
