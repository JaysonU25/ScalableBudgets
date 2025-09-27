from flask import Flask, request, jsonify
import os
from huggingface_hub import InferenceClient
app = Flask(__name__)

client = InferenceClient(
    provider="hf-inference",
    model="mistralai/Mixtral-8x7B-Instruct-v0.1",
    api_key="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",
)

@app.route("/", methods=["GET"])
def sayHello():
    prompt = "Bye"
    print(prompt)
    # Use text_generation for standard inference
    result = client.text_generation(prompt, max_new_tokens=128)
    print(result)
    return jsonify({"generated_text": result})

@app.route("/chat", methods=["POST"])
def chat():
    data = request.json
    prompt = data.get("inputs", "")
    print(prompt)
    # Use text_generation for standard inference
    result = client.text_generation(prompt, max_new_tokens=128)
    print(result)
    return jsonify({"generated_text": result})

if __name__ == "__main__":
    app.run(port=5005)