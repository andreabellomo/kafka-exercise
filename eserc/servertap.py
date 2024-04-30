import random
from flask import Flask
import json
app = Flask(__name__)

@app.route('/api/json',methods = ['GET'])
def get_json():
    id =  random.randint(1, 10)
    stato = random.randint(1,2)
    data = {
            "ID": id,
            "Status": stato
    }
    print(data)
    return json.dumps(data)

if __name__ == '__main__':
    app.run(host="0.0.0.0",port=5000)