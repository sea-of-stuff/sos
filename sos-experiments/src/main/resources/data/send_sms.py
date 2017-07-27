from twilio.rest import Client

# Your Account SID from twilio.com/console
account_sid = "ACb9ea2e6b3462156a34608229ca82411c"
# Your Auth Token from twilio.com/console
auth_token  = "f44fa27af6960329d9d2cf0070bebfc4"

client = Client(account_sid, auth_token)

message = client.messages.create(
    to="+447918140574", 
    from_="+441622321762",
    body="Ciao Amore mio! Ma lo sai che sei bellissima? <3")

print(message.sid)
