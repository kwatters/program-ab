
pipeline {

   // use linux for ssh or switch from ssh-agent to plain ssh
   agent  { label 'linux' }

    environment {
        pom = readMavenPom()
        VERSION = '0.0.8.7'
        GROUP_ID = 'program-ab'
        ARTIFACT_ID = '0.0.8.7'
    }

   options {
      // This is required if you want to clean before build
      // skipDefaultCheckout(true)
     skipDefaultCheckout(false)
   }


   tools {
      maven 'M3' // defined in global tools - maven is one of the only installers that works well for global tool
   // jdk 'openjdk-11-linux' // defined in global tools
   // git
   }

   stages {

      stage('clean') {
         steps {
            echo '====== clean ======'
            cleanWs()
         }
      }

      stage('init') {
         steps {
            echo '======= init ======='
            script {
               if (isUnix()) {
                  echo sh(script: 'env|sort', returnStdout: true)
               } else {
                  bat("set")
               }
            }
         }
      }

      stage('check out') {
         steps {
            echo '====== check out ======'
            checkout scm
         }
      }

      // FIXME - InMoov/version file needs to be created
      stage('package') {
         steps {
            script {
               echo '====== build ======'
               if (isUnix()) {
                  sh '''
                        echo "building ${JOB_NAME}..."
                        mvn package
                  '''
               } else {
                  bat('''
                        type "building ${JOB_NAME}..."
                        mvn package
                  ''')
               } // isUnix
            } // script
         } // steps
      } // stage
/*
     # not necessary to archive files - because the install step will copy the file up
     stage('archive') {
         steps {
            archiveArtifacts 'target/inmoov-'+ version +'.zip'
         }
      }
*/      

   } // stages

   post {
    success {
         echo "====== installing into repo ======"
         
         sshagent(credentials : ['myrobotlab2.pem']) {
               sh 'scp -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ./target/${ARTIFACT_ID}-${VERSION}.jar ubuntu@repo.myrobotlab.org:/home/ubuntu'
               sh '''
                  ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ubuntu@repo.myrobotlab.org sudo mvn install:install-file  -Dfile=${ARTIFACT_ID}-${VERSION}.jar \
                        -DgroupId=${GROUP_ID} \
                        -DartifactId=${ARTIFACT_ID} \
                        -Dversion=${VERSION} \
                        -Dpackaging=jar \
                        -DlocalRepositoryPath=/repo/artifactory/myrobotlab/
                  
                  ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ubuntu@repo.myrobotlab.org sudo mv \
                  /repo/artifactory/myrobotlab/${GROUP_ID}/${ARTIFACT_ID}/maven-metadata-local.xml \
                         /repo/artifactory/myrobotlab/${GROUP_ID}/${ARTIFACT_ID}/maven-metadata.xml

               '''

         } // sshagent
    } // success
  } // post
} // pipeline
