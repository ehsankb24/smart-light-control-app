# smart-light-control-app

in the res/strings.xml 
<string name="server_address">tcp://broker.emqx.io:1883</string>
is the address of the mqtt broker for app conection

@Serializable
data class BulbStateResponse(
    val isOn: Boolean,
    val brightness: Float
)
data model for send command to the bulb and get status of the bulb. use same data model

for send command to the bulb use "bulbOrder/$bulb_id" topic

for get bulb status in app subscribe to "bulbState/$bulb_id" topic

in the res/strings.xml
<string name="bulb_id">bulb_id</string>
is identifier of the bulb 
