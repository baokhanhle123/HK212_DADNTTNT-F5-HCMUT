import csv
import numpy as np
import os

def read_train():       # Save train pattern in data
    filename = input("Enter csv file: ")
    rows = []               
    with open(filename, 'r') as csvfile:
        csvreader = csv.reader(csvfile)
        for row in csvreader:
            rows.append(row)
        print("Total rows: %d"%(csvreader.line_num))
    for row in rows[19:]:
        if eval(row[12]) < 0.6:
            dataset_path = "./train/class1/"
        elif 0.6 <= eval(row[12]) < 6:
            dataset_path = "./train/class2/"
        elif 6 <= eval(row[12]) < 15:
            dataset_path = "./train/class3/"
        elif 15 <= eval(row[12]) < 50:
            dataset_path = "./train/class4/"
        else:
            dataset_path = "./train/class5/"
        file_name = str(row[0]) + "-" + str(row[1]) + "-" + str(row[2])
        Savefile = dataset_path + file_name + ".npy"
        while os.path.exists(Savefile):
            Savefile = dataset_path + file_name + ".npy"
        np.save(dataset_path + file_name, np.array([eval(row[3]), eval(row[11])]))
        print ("Dataset saved at : {}".format(dataset_path + file_name + '.npy'))
        
        
def train():            # For caculate mean,std in each class
    No_rain = []        # 0-0.6
    Small_rain = []     # 0.6-6
    Rain = []           # 6-15
    Normal_rain = []    # 16-50
    Big_rain = []       # 50-100
    for i in range(1,6):
        dataset_path = "./train/" + "class" + str(i) + "/"
        for fx in os.listdir(dataset_path):
            if fx.endswith('.npy'):
                data_item = np.load(dataset_path + fx)
                if i == 1: No_rain.append(data_item)
                elif i == 2: Small_rain.append(data_item)
                elif i == 3: Rain.append(data_item)
                elif i == 4: Normal_rain.append(data_item)
                elif i == 5: Big_rain.append(data_item)
                
    result = []
    if len(No_rain) > 0:
        result.append([np.mean([i[0] for i in No_rain]), 
                   np.std([i[0] for i in No_rain]),
                   np.mean([i[1] for i in No_rain]), 
                   np.std([i[1] for i in No_rain])])
    if len(Small_rain) > 0:
        result.append([np.mean([i[0] for i in Small_rain]), 
                    np.std([i[0] for i in Small_rain]),
                    np.mean([i[1] for i in Small_rain]), 
                    np.std([i[1] for i in Small_rain])])
    if len(Rain) > 0:
        result.append([np.mean([i[0] for i in Rain]), 
                    np.std([i[0] for i in Rain]),
                    np.mean([i[1] for i in Rain]), 
                    np.std([i[1] for i in Rain])])
    if len(Normal_rain) > 0:
        result.append([np.mean([i[0] for i in Normal_rain]), 
                    np.std([i[0] for i in Normal_rain]),
                    np.mean([i[1] for i in Normal_rain]), 
                    np.std([i[1] for i in Normal_rain])])
    if len(Big_rain) > 0:
        result.append([np.mean([i[0] for i in Big_rain]), 
                    np.std([i[0] for i in Big_rain]),
                    np.mean([i[1] for i in Big_rain]), 
                    np.std([i[1] for i in Big_rain])])
    for i in range(len(result)):
        print("\nClass" + str(i+1) + ": ", end =" ") 
        print(result[i])
    for i in range(len(result)):
        dataset_path = "./train/result/"
        a = i + 1
        file_name = "class" + str(a)
        Savefile = dataset_path + file_name + ".npy"
        while os.path.exists(Savefile):
            Savefile = dataset_path + file_name + ".npy"
        np.save(dataset_path + file_name, np.array(result[i]))
        print ("\nDataset saved at : {}".format(dataset_path + file_name + '.npy'))
    
    
def main():
    print("1. Read train pattern")
    print("2. Process train pattern")
    choose = input("Read train pattern or process train pattern: ")
    if choose == "1":
        read_train()
    else:
        train()
main()