import numpy as np
import os
import math

#==============================Gauss==============================
def Gauss(temperature, humidity):
    classifier = {}
    probability = {}
    dataset_path = "./train/result/"
    for fx in os.listdir(dataset_path):
        if fx.endswith('.npy'):
            name = fx[:-4]
            classifier[name] = np.load(dataset_path + fx)
            probability[name] = 0.2 * \
                        1 / math.sqrt(2 * math.pi * classifier[name][1]) * \
                        (math.e ** ( - (temperature - classifier[name][0])**2 / (2*classifier[name][1]**2))) \
                        * 1 / math.sqrt(2*math.pi*classifier[name][3]) * \
                        (math.e ** (-(humidity - classifier[name][2])**2 / (2*classifier[name][3]**2)))

    dk = dict(sorted(probability.items(), key=lambda item: item[1]))
    return list(dk)[-1]



#==============================K-NN=================================
def takeSecond(elem):               # For sorted list
    return elem[1]

def insert(list, element, k):       # For find k element min when insert new element to list
    if len(list) < k:
        list.append(element)
        list.sort(key=takeSecond)
    elif list[-1] > element:
        list[-1] = element
        list.sort(key=takeSecond)
    return list
 
def distance(v1, v2):               # Euclid distance
    return np.sqrt(((v1-v2)**2).sum())
    
def KNN(temperature, humidity, k):  # KNN
    list = []
    for i in range(1,6):
        dataset_path = "./train/class" + str(i) +"/"
        for fx in os.listdir(dataset_path):
            if fx.endswith('.npy'):
                data = np.load(dataset_path + fx)
                insert(list, ("class" + str(i), distance(data,np.array([temperature, humidity]))), k)
    labels = np.array(list)[:, 0]                           # k class nearest test
    output = np.unique(labels, return_counts=True)          # Frequency of each class
    index = np.argmax(output[1])                            # Index of class have the most element near test
    return output[0][index]
    
#print(Gauss(27,73.5))
#print(KNN(27,73.5,5))