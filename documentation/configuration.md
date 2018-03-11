## SOS Node Configuration

The configuration of a SOS node is specified using a simple JSON structure.

See also the [example_config.json](../example_config.json)

### Example

```json
{
  "settings": {
    "services": {
      "storage": {
        "exposed": true,
        "canPersist": true,
        "maxReplication": 3
      },
      "cms": {
        "exposed": false,
        "automatic": true,
        "predicateThread": {
          "initialDelay": 10,
          "period": 20

        },
        "policiesThread": {
          "initialDelay": 15,
          "period": 20
        },
        "checkPoliciesThread": {
          "initialDelay": 100,
          "period": 60
        },
        "getdataThread": {
          "initialDelay": 60,
          "period": 60
        },
        "spawnThread": {
          "initialDelay": 90,
          "period": 120
        }
      },
      "mds": {
        "exposed": true,
        "maxReplication": 3
      },
      "rms": {
        "exposed": false
      },
      "nds": {
        "exposed": false,
        "startupRegistration": true,
        "bootstrap" : true,
        "ping": true
      },
      "mms": {
        "exposed": false
      },
      "experiment": {
        "exposed": false
      }
    },
    "rest": {
      "port": 8080
    },
    "webDAV": {
      "port": 8081
    },
    "webAPP": {
      "port": 8082
    },
    "keys": {
      "location": "~/sos/keys/"
    },
    "store": {
      "type": "local",
      "location": "~/sos/"
    },
    "global": {
      "tasks": {
        "thread": {
          "ps": 4
        }
      },
      "nodeMaintainer": {
        "enabled": true,
        "maxSize": 1048576,
        "thread": {
          "ps": 1,
          "initialDelay": 60,
          "period": 60
        }
      }
    },
    "bootstrapNodes": [
      {
      "guid" : "SHA256_16_bb077f9420219e99bf776a7a116334405a81d2627bd4f87288259607f05d1615",
      "hostname" : "138.251.207.87",
      "port" : 8080,
      "certificate" : "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKZOnoFAxsx4BiXBKzeJISOv5q5XTSpPZRCmYGg+59VctY1xeYS7NEkEmbk/Sa8y5chrZttN5CggdBJBIFGgMU0CAwEAAQ=="
      }
    ]
  }
}
```


### Finding the Java cacerts path

The cacerts file is a collection of trusted certificate authority (CA) certificates.
Sun Microsystems™ includes a cacerts file with its SSL support in the Java™ Secure Socket Extension (JSSE) tool kit and JDK 1.4.x.
It contains certificate references for well-known Certificate authorities, such as VeriSign™.

The cacerts file is needed to allow the node to make HTTPS (HTTP with SSL) requests.

#### Linux

`$(readlink -f /usr/bin/java | sed "s:bin/java::")lib/security/cacerts`

#### MacOSX

`$(/usr/libexec/java_home)/jre/lib/security/cacerts`
