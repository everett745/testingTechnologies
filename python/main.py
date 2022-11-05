import csv
import pymongo

CSV_PATH = './dataSource.csv'

def getUsersCol():
    myclient = pymongo.MongoClient("mongodb://localhost:27017/")
    mydb = myclient["TestingTechnologies"]
    users = mydb["User"]
    return users

def getUsersFromCsv(filePath):
    d = []
    input_file = csv.DictReader(open(filePath))
    for row in input_file:
        v = {}
        for key in row.keys():
            newKey = '_id' if key == 'ID' else key.lower()
            v[newKey] = row[key]
        d.append(v)

    return d

def insertUsersToMong(data):
    col = getUsersCol()
    col.insert_many(data)

def clearUsersTable():
    getUsersCol().drop()

if __name__ == '__main__':
    clearUsersTable()
    data = getUsersFromCsv(CSV_PATH)
    insertUsersToMong(data)
