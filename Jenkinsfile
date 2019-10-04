import org.apache.commons.io.FileUtils
library identifier: 'pipeline-helper@master', retriever: modernSCM(
  [$class: 'GitSCMSource',
       remote: 'https://gitlab.com/papanito/jenkins-pipeline-helper.git',
       credentialsId: 'bitbucket.service.user'
  ])


ciPipelineMaven {
    nodes     = 'default' /* label of jenkins nodes*/
    strIqApp  = 'jenkins-pipeline-helper'
    email     = 'no-reply@wyssmann.com' /* group mail for notifications */
}