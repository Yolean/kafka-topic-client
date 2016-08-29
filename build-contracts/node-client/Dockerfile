FROM node:6

WORKDIR /usr/src/app

COPY package.json ./

RUN npm install

COPY ./ ./

ENV TOPIC_NAME "build-contract-test"

CMD node index.js