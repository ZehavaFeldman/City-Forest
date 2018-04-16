const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions

const algoliasearch = require('algoliasearch');
const algoliaFunctions = require('algolia-firebase-functions');

 const algolia = algoliasearch(functions.config().algolia.app,
                 functions.config().algolia.key);
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
  
  // Specify Algolia's objectID using the Firebase object key
  record.objectID = childRef.data.key;
  // Add or update object
  return index 
    .saveObject([record])
    .then(() => {
      console.log('Firebase object indexed in Algolia', record.objectID);
    })
    .catch(error => {
      console.error('Error when indexing contact into Algolia', error);
      process.exit(1);
    });      
}

function deleteIndexRecord(childRef) {
  // Get Algolia's objectID from the Firebase object key
  const objectID = childRef.data.key; 
  // Remove the object from Algolia
  return index
    .deleteObject(objectID)
    .then(() => {
      console.log('Firebase object deleted from Algolia', objectID);
    })
    .catch(error => {
      console.error('Error when deleting contact from Algolia', error);
      process.exit(1);
    });
}

function addOrUpdateIndexRecordPoints(pointChildRef) {
  console.log("The function addOrUpdateIndexRecord is running!");
  // Get Firebase object
console.log('pointChildRef.data.key', pointChildRef.data.key);
console.log('pointChildRef.data.val', pointChildRef.data.val());  
const record = pointChildRef.data.val(); 
delete record.position; 
  // Specify Algolia's objectID using the Firebase object key
  record.objectID = pointChildRef.data.key;
  // Add or update object
  return indexPoints 
    .saveObject(record)
    .then(() => {
      console.log('Firebase point object indexed in Algolia', record.objectID);
    })
    .catch(error => {
      console.error('Error when indexing contact into Algolia', error);
      process.exit(1);
    });      
}

function deleteIndexRecordPoints(pointChildRef) {
  // Get Algolia's objectID from the Firebase object key
  const objectID = pointChildRef.data.key; 
  // Remove the object from Algolia
  return indexPoints
    .deleteObject(objectID)
    .then(() => {
      console.log('Firebase object deleted from Algolia', objectID);
    })
    .catch(error => {
      console.error('Error when deleting point from Algolia', error);
      process.exit(1);
    });
}




