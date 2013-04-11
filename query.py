from flask import Flask, request, jsonify
import json
import generate_page


app = Flask(__name__)


@app.route("/query/")
def query():
	plan = request.args.get('plan')
	# plan = 'hello world'
	return generate_page.results(plan)


if __name__ == "__main__":
    # Debug mode should be turned off when you are finished
    app.run(debug=True)
