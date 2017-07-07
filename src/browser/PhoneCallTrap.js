module.exports = function () {
    var sucessCallback = function (value) {

    }
    var failCallback = function (value) {

    }
    var changeState = function(state, incomingNumber){
        if(sucessCallback)
            sucessCallback({state:state,incomingNumber:incomingNumber})
    }
    var submitIDLE = function () {
        changeState('IDLE', '')
    }
    var submitOFFHOOK = function () {
        changeState('OFFHOOK', '12345678')
    }
    var submitRINGING = function () {
        changeState('RINGING', '87654321')
    }
    return {
        changeState:changeState,
        submitIDLE:submitIDLE,
        submitOFFHOOK:submitOFFHOOK,
        submitRINGING:submitRINGING,
        onCall:function (success_cb,fail_cb) {
            sucessCallback = success_cb
            failCallback = fail_cb
            submitIDLE()
        }
    }
}
