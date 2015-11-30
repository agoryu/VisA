import matplotlib.pyplot as plt
import matplotlib.patches as mpatches

#Calcul des fonction basse, moyenne et eleve
def CalcTempB(i):
    if(i < 10):
        return 1.0
    elif(i<20):
        return 1.0 - ((1.0/10.0) * (i-10.0))
    else:
        return 0.0

def CalcTempM(i):
    if(i < 10):
        return 0.0
    elif(i<20):
        return ((1.0/10.0) * (i-10.0))
    elif(i<30):
        return 1.0 - ((1.0/10.0) * (i-20.0))
    else:
        return 0.0

def CalcTempE(i):
    if(i < 20):
        return 0.0
    elif(i<30):
        return ((1.0/10.0) * (i-20.0))
    else:
        return 1.0

#Calcul des operateurs de la logique flou
def Opmin(tab1, tab2):
    size = min(len(tab1), len(tab2))
    size = range(0, size)
    result = []
    for i in size:
        step = [tab1[i], tab2[i]]
        result.append(min(step))

    return result

def Opmax(tab1, tab2):
    size = min(len(tab1), len(tab2))
    size = range(0, size)
    result = []
    for i in size:
        step = [tab1[i], tab2[i]]
        result.append(max(step))

    return result

def Chauf(i):
    if (i<8):
        return 0
    elif (i<10):
        return i*(1/2)
    else:
        return 1

def ChaufTemp(tab, mini):
    result = []
    for i in tab:
        result.append(min(i, mini))
    return result

#calcul de la courbe
temp = range(0, 40)
puissanceChauf = range(0,15)
temperatureB = []
temperatureM = []
temperatureE = []
chauffe = []

for i in temp:
    temperatureB.append(CalcTempB(i))
    temperatureM.append(CalcTempM(i))
    temperatureE.append(CalcTempE(i))

for i in puissanceChauf:
    chauffe.append(Chauf(i))

#Exo 1

print("degre d'appartenance pour la temperature 16 :")
print("basse = " + repr(temperatureB[16]))
print("moyen = " + repr(temperatureM[16]))
print("eleve = " + repr(temperatureE[16]))

plt.title("Partition floue de la temperature")
plt.plot(temp, temperatureB, label='bas')
plt.plot(temp, temperatureM, label='moyen')
plt.plot(temp, temperatureE, label='eleve')
plt.xlabel('Temperature')
plt.axis([0, 40, -0.1, 1.1])
plt.legend()
plt.show()


#Exo 2

minTemp = Opmin(temperatureB, temperatureM)
maxTemp = Opmax(temperatureM, temperatureE)

plt.title("Operateur de la logique floue")
plt.plot(temp, minTemp, label='min')
plt.plot(temp, maxTemp, label='max')
plt.xlabel('Temperature')
plt.axis([0, 40, -0.1, 1.1])
blue_patch = mpatches.Patch(color='blue')
green_patch = mpatches.Patch(color='green')
plt.legend()
plt.show()


#Chauffage

plt.title("Chauffage")
plt.plot(puissanceChauf, chauffe)
plt.xlabel('Puissance chauffage')
plt.axis([0, 15, -0.1, 1.1])
plt.show()


#Exo 3

result = ChaufTemp(chauffe, 0.8)
plt.title("Implication de Mamdani")
plt.plot(range(0, len(result)), result)
plt.xlabel('Puissance chauffage')
plt.axis([0, 15, -0.1, 1.1])
plt.show()
