import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from sklearn.preprocessing import scale
from sklearn.model_selection import cross_val_score
from sklearn.naive_bayes import GaussianNB
from sklearn.neighbors import KNeighborsClassifier

################################### Collect Data + Preprocessing ##################################################

main_df = pd.read_csv('POWER_Point_Daily_20190101_20211231_010d7269N_106d6897E_LST.csv', skiprows=18)

main_df = main_df[['T2M', 'RH2M', 'PRECTOTCORR']] # Lấy các thuộc tính cần thiết

### Begin: Chia ngưỡng ###

plist = np.array(list(main_df['PRECTOTCORR']))

newplist = []

for p in plist:
    if (p<6): newplist.append('low')
    elif (6<=p and p<16): newplist.append('<medium')
    elif (16<=p and p<50): newplist.append('medium')
    # elif (50<=p and p<100): newplist.append('high')
    else: newplist.append('high')

main_df['NEW_PRECTOTCORR'] = newplist

### End: Chia ngưỡng ###

main_df = main_df[['T2M', 'RH2M', 'NEW_PRECTOTCORR']]

X, y = np.array(main_df.iloc[:, :-1]), np.array(main_df.iloc[:, -1])

# X: tập các feature, y: Target

########################################### KNN #####################################################################

y_list_for_knn = []

for k in range(1 ,100):

    knn = KNeighborsClassifier(n_neighbors=k)

    cv_result = cross_val_score(knn, X, y, cv = 5)

    # print(np.mean(cv_result))
    y_list_for_knn.append(np.mean(cv_result))

# Với cv = 5, acuracy cao nhất khi i = 62(k=62): 76.9161%

plt.plot(range(1,100), y_list_for_knn)

plt.xlabel('Number of K')
plt.ylabel("Accuracy")
plt.title("Accuracy by K")

plt.show()
 
############################################# Gauss ###############################################################

gnb = GaussianNB()

cv_result = cross_val_score(gnb, X, y, cv = 5)

print("GNB result: " + str(np.mean(cv_result)))

###################################################################################################################

color = {

}

i = 0

for label in y:
    if not(label in color.keys()):
        color[label] = i
        i = i + 1

sublist = [color[label] for label in y]

plt.scatter(np.array(main_df['T2M']), np.array(main_df['RH2M']), c=sublist)
plt.xlabel('Tem (C)')
plt.ylabel('Hum (%)')
plt.show()