#### How to start the service using Docker

To start a new instance from the project with the Plugin enabled run:

```
dockerComposeUp
```

#### How to start using SBT

```
sbt run
```

#### To run tests using SBT

```
sbt test
```

#### Following end points are supported by the API

Method - POST
URL - http://localhost:5000/images
Body type - application/json
Valid json -

```
{
    "images": [
        "https://clarifai.com/developer/static/images/model-samples/general-001.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/7/7b/Acer_Aspire_8920_Gemstone.jpg",
        "https://gilbertcolvin.co.uk/redbridge/primary/gilbertcolvin/arenas/websitecontent/web/documents.jpg"
    ]
}
```

**Json body should contain a node called images which is an array of strings**

*Sample response for above request*

```
{
    "groupedImages": {
        "desktop": [
            "https://upload.wikimedia.org/wikipedia/commons/7/7b/Acer_Aspire_8920_Gemstone.jpg"
        ],
        "alcohol": [],
        "documents": [
            "https://gilbertcolvin.co.uk/redbridge/primary/gilbertcolvin/arenas/websitecontent/web/documents.jpg"
        ]
    }
}
```

**All responses are grouped under a node called groupedImages**

## How are images classified

Clarifai API general model is used to derive predicted **concepts** for an image. Further to that, the API applies its own logic.

All images with following concept are grouped under **alcohol**

1.wine
2.bar
3.alchol
4.winery

All images with following concept are grouped under **computer**

1.computer
2.laptop
3.monitor
4.screen

All images with following concept are grouped under **documents**

1.file
2.document