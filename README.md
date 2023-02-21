# Operativo Funeraria


--------------------------------------------
## API RESPONSES Y REQUESTS
# url: "https://nose/"


///////////////////////////////////////////////////////////////////////////////
### Login [/login]

### Login [POST]


+ Request (application/x-www-form-urlencoded)

        username=admin&password=admin

+ Response 200 (application/json)

        {
                "status_code": "200",
                "auth_token": "dsadaddadsdasdssdasdZ",
                "user":{
                          "id": 0,
                          "username": "Christian Magaña",
                          "role": "OPERATIVO",
                          "autoPlaca": "JSK-23-L",
                          "channel_tracking": "channel_124"
                       },
                 "servicio":{
                                "id_servicio": "21321",
                                "reco_lat": 20.6733272,
                                "reco_lng": -103.384734,
                                "reco_name": "San Javier",
                                "reco_address": "Av Pablo Casals 640",
                                "cliente": "Juan Orozco",
                                "telefono": "+54 324234324",
                                "tipo_cliente": "Directo",
                                "plan": "Medera Italia"
                }       
        }


///////////////////////////////////////////////////////////////////////////////
### Servicio [/servicio{?id}]

 + Headers
 
		"Authorization": "Bearer xxxxxxxxxx"

 + Parameters
 
		+ id (number) - ID del usuario


### Servicio [GET]

+ Response 200 (application/json)

        {
                "status_code": "200",
                 "servicio":{
                                "id_servicio": "3333",
                                "reco_lat": 20.6733272,
                                "reco_lng": -103.384734,
                                "reco_name": "San Miguel",
                                "reco_address": "Av La Tuzania",
                                "cliente": "Christian Magaña",
                                "telefono": "+54 21342243",
                                "tipo_cliente": "Directo",
                                "plan": "Medera Italia"
                        }       
        }
        

///////////////////////////////////////////////////////////////////////////////
### FinalizarReco [/finalizar-reco{?idUser,idServicio,code}]

+ Headers

		"Authorization": "Bearer xxxxxxxxxx"

 + Parameters
 
		+ idUser (number) - ID del usuario
		+ idServicio (numbre) - ID del servicio
		+ code (text) - Codigo proporsionado por el cliente
    


### FinalizarReco [GET]

+ Response 200 (application/json)

        {
                "status_code": "200",
                "is_finalized": true,
                "message": "Se finalizó la recolección correctamente"
        }

 ///////////////////////////////////////////////////////////////////////////////
## Version [/version-app]

### Version [GET]

+ Response 200 (application/json)

        {
                "status_code": "200",
                 "version_code": 1,
                 "version_name": "1.0"
        }

