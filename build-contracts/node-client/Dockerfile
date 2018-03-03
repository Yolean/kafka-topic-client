FROM yolean/node@sha256:5d4f720b200a693e00b354ce9eef0688a994a80d54bc74b122e346ca8b54d76c

WORKDIR /usr/src/app

COPY package.json ./

RUN npm install

COPY ./ ./

ENV TOPIC_NAME "build-contract-test"

CMD node index.js