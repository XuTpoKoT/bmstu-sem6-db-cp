import datetime
import csv
import string

from random import randint, choice
from faker import Faker
from uuid import uuid4
import json

def readIdsCsv(filename):
    ids = []
    with open(filename, newline='') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            ids.append(row['id'])

    return ids

def readIdsNamesCsv(filename):
    res = []
    with open(filename, newline='') as csvfile:
        reader = csv.DictReader(csvfile)
        for row in reader:
            res.append({'id': row['id'], 'name_': row['name_']})

    return res

def genManufactures():
    names = [line.strip() for line in open("txt/manufacturers.txt", "r")]

    with open('manufacturers.csv', 'w', newline='') as csvfile:
        fieldnames = ['id', 'name_']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()

        for name in names:
            id = uuid4()
            writer.writerow({'id': id, 'name_': name})

def genProducts():    
    def genProductValues(fieldValues):
        def genProductName(manufacturer):
            return manufacturer + ' ' + ''.join(choice(string.ascii_uppercase) for _ in range(3)) + \
                   '-' + ''.join(choice(string.digits) for _ in range(4))

        def genDescription(category):
            if category == 'Гитары':
                return 'Крутая гитара'
            elif category == 'Синтезаторы':
                return 'Крутой синтезатор'
        
        def genGuitarCharacteristics(fieldValues):
            characteristics = {}
            characteristics["Материал корпуса"] = choice(materials)
            characteristics["Материал деки"] = choice(materials)
            characteristics["Кол-во ладов"] = choice([15, 18, 24])
            ch = json.dumps(characteristics, ensure_ascii=False)
            fieldValues['characteristics'] = ch
            

        def genSynthesizerCharacteristics(fieldValues):
            characteristics = {}
            characteristics["Кол-во клавиш"] = choice([44, 49, 61, 76])
            characteristics['Кол-во стилей'] = randint(100, 800)
            characteristics['Кол-во тембров'] = randint(100, 400)
            ch = json.dumps(characteristics, ensure_ascii=False)
            fieldValues['characteristics'] = ch

        manufacturer = choice(manufacturers)
        category = choice(['Гитары', 'Синтезаторы'])

        fieldValues['id'] = uuid4()
        fieldValues['name_'] = genProductName(manufacturer['name_'])
        fieldValues['description'] = genDescription(category)
        fieldValues['price'] = randint(5000, 25000)
        # fieldValues['storage_cnt'] = randint(1, 20)
        fieldValues['color'] = choice(colors)
        fieldValues['manufacturer_id'] = manufacturer['id']        

        if category == 'Гитары':
            genGuitarCharacteristics(fieldValues)
        elif category == 'Синтезаторы':
            genSynthesizerCharacteristics(fieldValues)            

    cntRecords = 100
    manufacturers = readIdsNamesCsv('manufacturers.csv')
    colors = [line.strip() for line in open("txt/colors.txt", "r")]
    materials = [line.strip() for line in open("txt/materials.txt", "r")]

    for m in manufacturers:
        print(m)

    with open('ProductModel.csv', 'w', newline='') as csvfile:
        fieldNames = ['id', 'name_', 'price', 'description', 'color', 'manufacturer_id', 'characteristics']
        writer = csv.DictWriter(csvfile, fieldnames=fieldNames)
        writer.writeheader()

        for i in range(cntRecords):
            fieldValues = {field: None for field in fieldNames}
            genProductValues(fieldValues)
            writer.writerow(fieldValues)

def genOrders():
    cntRecords = 2500
    statuses = readIdsCsv('order_statuses.csv')
    customerIds = readIdsCsv('customers.csv')

    with open('Orders.csv', 'w', newline='') as csvfile:
        fieldnames = ['id', 'customer_id', 'date', 'status']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()

        for i in range(cntRecords):
            id = uuid4()
            writer.writerow({'id': id,
                             'customer_id': choice(customerIds),
                             'date': fake.date_between(datetime.date(2005, 1, 1), datetime.date(2021, 12, 31)),
                             'status': choice(statuses)})

def genOrder_ProductModel():
    cntRecords = 2500
    orderIds = readIdsCsv('Orders.csv')
    productModelIds = readIdsCsv('ProductModel.csv')

    with open('Order_ProductModel.csv', 'w', newline='') as csvfile:
        fieldnames = ['order_id', 'product_model_id', 'cnt_products']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
        writer.writeheader()

        for i in range(cntRecords):
            writer.writerow({'order_id': choice(orderIds),
                             'product_model_id': choice(productModelIds),
                             'cnt_products': randint(1, 20)})

if __name__ == "__main__":
    fake = Faker()
    genManufactures()
    genProducts()
    # genOrders()
    # genOrder_ProductModel()
