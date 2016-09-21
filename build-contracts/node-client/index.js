const Kafka = require('no-kafka');

const CONNECTION_STRING = 'kafka:9092';

const options = {
  connectionString: CONNECTION_STRING
};

const TOPIC_NAME = process.env.TOPIC_NAME;
if (!TOPIC_NAME) throw new Error('Missing topic name!');

const producer = new Kafka.Producer(options);

function test(retriesLeft) {
  producer.init().then(() =>
      producer.send({
        topic: TOPIC_NAME,
        partition: 0,
        message: {
          value: 'Hi new topic!'
        }
      }))
    .then(results => {
      results.forEach(result => {
        if (result.error) {
          console.error(result.error);
          if (retriesLeft <= 0) process.exit(1);
          else setTimeout(test.bind(null, retriesLeft - 1), 5000);
          return;
        }

        console.log('Produced a message with non-error result', result);
        process.exit();
      });
    })
    .catch(err => {
      console.error(err);
      console.error('Failed to communicate with kafka topic. Retries left: ' + retriesLeft);
      if (retriesLeft <= 0) process.exit(1);
      else setTimeout(test.bind(null, retriesLeft - 1), 5000)
    });
}

test(10);