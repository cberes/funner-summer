const axios = require('axios');

const apiKey = 'TODO_REPLACE_ME';
const baseUrl = 'http://api.openweathermap.org/data/2.5';

function successResponse(responseBody, callback) {
  callback(null, {
    statusCode: 200,
    body: JSON.stringify(responseBody)
  });
}

function errorResponse(errorMessage, awsRequestId, callback) {
  callback(null, {
    statusCode: 500,
    body: JSON.stringify({
      error: errorMessage,
      reference: awsRequestId,
    })
  });
}

/** Rounds the value to the nearest of 0.00, 0.05, or 0.10. */
function round(value) {
  return Math.round(value * 20.0) / 20.0;
}

function summarize(weather) {
  return {
    condition: weather.weather[0].main,
    temperature: weather.main.temp
  };
}

exports.handler = (event, context, callback) => {
  const latitude = event.queryStringParameters.lat;
  const longitude = event.queryStringParameters.lng;
  const summary = event.queryStringParameters.summary || false;

  axios.get(`${baseUrl}/weather`, {
      params: {
        lat: round(latitude),
        lon: round(longitude),
        units: 'imperial'
      },
      headers: {
        'x-api-key': apiKey
      }
    })
    .then(response => {
      const weather = response.data;
      successResponse(summary ? summarize(weather) : weather, callback);
    })
    .catch(err => {
      console.error(err);
      errorResponse(err.message, context.awsRequestId, callback);
    });
};

