const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions

const algoliasearch = require('algoliasearch');
const algoliaFunctions = require('algolia-firebase-functions');

 const algolia = algoliasearch(functions.config().algolia.app,
                 functions.config().algolia.adminkey);
 const index = algolia.initIndex(functions.config().algolia.index);
 const indexPoints = algolia.initIndex(functions.config().algolia.index_points);
  
/*                              
 exports.syncAlgoliaWithTracks = functions.database.ref('/tracks').onWrite(
                                event => algoliaFunctions.syncAlgoliaWithFirebase(index, event)
                                 );

 exports.syncAlgoliaWithPoints = functions.database.ref('/points_of_interest').onWrite(
                                 event => algoliaFunctions.syncAlgoliaWithFirebase(pointsIndex, event)

                                 );

*/
const tracksRef = functions.database.ref('/tracks/{trackID}');

exports.trackAdded=tracksRef.onCreate(addOrUpdateIndexRecord);
exports.trackChanged=tracksRef.onUpdate(addOrUpdateIndexRecord); 
exports.trackRemoved=tracksRef.onDelete(deleteIndexRecord);

const pointsRef = functions.database.ref('/points_of_interest/{pointID}');

exports.pointsAdded=pointsRef.onCreate(addOrUpdateIndexRecordPoints);
exports.pointsChanged=pointsRef.onUpdate(addOrUpdateIndexRecordPoints); 
exports.pointsRemoved=pointsRef.onDelete(deleteIndexRecordPoints);

function addOrUpdateIndexRecord(childRef) {
  console.log("The function addOrUpdateIndexRecord is running!");
// Get Firebase object
const record = childRef.data.val(); 
  console.log("record after intialization", record);
  // Specify Algolia's objectID using the Firebase object key
  record.objectID = childRef.data.key;
console.log("route",record.route);
delete record.route;
delete record.waypoints;
delete record.points;
console.log("objectID", record.objectID);
// Add or update object
console.log("Adding RECORD", record);
return index.addObject(record);
}

function deleteIndexRecord(childRef) {
  // Get Algolia's objectID from the Firebase object key
  const objectID = childRef.data.key; 
  // Remove the object from Algolia
  return index.deleteObject(objectID)
}

function addOrUpdateIndexRecordPoints(pointChildRef) {
  console.log("The function addOrUpdateIndexRecord is running!");
  // Get Firebase object
console.log('pointChildRef.data.key', pointChildRef.data.key);
console.log('pointChildRef.data.val', pointChildRef.data.val());  
const record = pointChildRef.data.val(); 

//delete record.position; 
  // Specify Algolia's objectID using the Firebase object key
  record.objectID = pointChildRef.data.key;
     console.log("Algolia record", record);
// Add or update object
  return indexPoints.addObject(record);
}

function deleteIndexRecordPoints(pointChildRef) {
  // Get Algolia's objectID from the Firebase object key
  const objectID = pointChildRef.data.key; 
  // Remove the object from Algolia
  return indexPoints.deleteObject(objectID)
}




