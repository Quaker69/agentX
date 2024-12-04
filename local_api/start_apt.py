from flask import Flask, request, jsonify

app = Flask(__name__)

# Define the custom API key
API_KEY = "23423jhagksjdg76&"

@app.route('/api/<api_key>', methods=['GET'])
def api_endpoint(api_key):
    # Check if the provided API key matches the custom API key
    if api_key == API_KEY:
        # Return a JSON response with status true
        return jsonify({"status": True})
    else:
        # Return a JSON response with status false
        return jsonify({"status": False})

if __name__ == '__main__':
    # Run the Flask web server
    app.run(debug=True)
