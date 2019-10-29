'use strict'
// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp(functions.config.firebase);

exports.sendNotification = functions.firestore.document('notifications/{notification_id}').onWrite ((change, context) => {
    const notificationId = context.params.notification_id;

    return admin.firestore().collection("notifications").doc(notificationId).get().then(queryResult=> {
        //get notification data
        const senderId = queryResult.data().senderId;
        const receiverId = queryResult.data().receiverId;
        const notificationMessage = queryResult.data().message;
        //get sender and receiver data
        const senderData = admin.firestore().collection("users").doc(senderId).get();
        const receiverData = admin.firestore().collection("users").doc(receiverId).get();

        return Promise.all([senderData, receiverData]).then(results =>{
            const senderName = results[0].data().name;
            const receiverName = results[1].data().name;
            const receiverTokenId = results[1].data().token_id;

            console.log("From :"+ senderName +" To: "+ receiverName);

            const payLoad = { notification: {
                    title : "Notification From: " + senderName,
                    body : notificationMessage,
                    icon : "default",
                    click_action : "com.exemple.firebasepushnotifications.TARGETNOTIFICATION"
                },
                data:{
                    notificationMessage : notificationMessage,
                    senderId : senderId
                }
            };
            return admin.messaging().sendToDevice(receiverTokenId, payLoad).then(results=>{
                console.log("Notification sent from recovered application")
                return true;
            });
        });
    });
});