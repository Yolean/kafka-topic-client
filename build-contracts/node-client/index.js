const Kafka = require('no-kafka');

const CONNECTION_STRING = 'kafka:9092';

const options = {
  connectionString: CONNECTION_STRING
};

const TOPIC_NAME = process.env.TOPIC_NAME;
if (!TOPIC_NAME) throw new Error('Missing topic name!');

const producer = new Kafka.Producer(options);

Promise.all([producer.init()]).then(() =>
    producer.send({
      topic: TOPIC_NAME,
      partition: 0,
      message: {
        value: 'Hi new topic!'
      }
    }))
  .then(results => {
    results.forEach(result => {
      if (result.error) throw result.error;
    });
  })
  .catch(err => {
    console.error(err);
    process.exit(1);
  });