const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

//const admin = require("firebase-admin");
//admin.initalizeApp(functions.config().firebase);
//
const algoliasearch = require('algoliasearch');
const algoliaFunctions = require('algolia-firebase-functions');
 
 const algolia = algoliasearch(functions.config().algolia.app,
                 functions.config().algolia.key);
 const index = algolia.initIndex(functions.config().algolia.index);
 const pointsIndex = algolia.initIndex(functions.config().algolia.index_points);
                                
 exports.syncAlgoliaWithTracks = functions.database.ref('/tracks').onWrite(
                                 event => algoliaFunctions.syncAlgoliaWithFirebase(index, event)
                                 );

 exports.syncAlgoliaWithPoints = functions.database.ref('/points_of_intereset').onWrite(
                                 event => algoliaFunctions.syncAlgoliaWithFirebase(pointsIndex, event)

                                 );



