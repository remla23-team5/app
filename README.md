# app

The app contains the code for running the restaurant sentiment analysis web app 

## Instructions for running this project

Clone this repository and execute the following commands in its root directory:

set the following environment variables for GitHub authentication, in order to be able to download the lib:
- `USERNAME`
- `PASSWORD`

The following command can be run to build the package:
```sh
mvn -s ./settings.xml clean package
```

After which a docker image can be built and run as follows:
```bash
docker build . -t ghcr.io/remla23-team5/app:0.0.1 --build-arg model_service_url=http://localhost:8080/predict
docker run -it --rm -p8000:8000 ghcr.io/remla23-team5/app:0.0.1
```

