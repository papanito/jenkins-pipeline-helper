/*
 * Returns the pipeline parameters from environment variable
 * retrun pipelineParams as map
 */

def call() {
    // Take the String value between the { and } brackets.
    def params =  
    "${env.pipelineParams}"[1..-2]
        .split(', ')
        .collectEntries { entry -> 
            def pair = entry.split('=')
            [(pair.first()): pair.last()]
        }

    echo "[INFO] pipelineParams are"
    params.each{ k, v -> println "${k}:${v}" }
    return params
}


/*
 * Adds an additional parameter to the pipelineParams environment variable
 * @param key name of the parameter to add
 * @param value value of the key
 */

def addValue(key, value) {
    pipelineParams = this.call()
    pipelineParams[key] = value
    env.pipelineParams = pipelineParams
}