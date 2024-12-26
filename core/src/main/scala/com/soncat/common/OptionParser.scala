package soncat.common


object OptionParser {
    // Args is an Array of strings
    // For each element, the option is specified by a string that starts with "--"
    // Its value is the element after "=". So `--port=8080` would be parsed as `port -> 8080`
    // This functions, based on the above, returns a Map of the options and their values
    // []String{"--port==8080"} -> Map{"port" -> "8080"}
    // Returns true as value for an option without one
    def parseOptions(args: Array[String]): Map[String, String] = {
        var options = Map[String, String]()

        args.foreach(
            arg => {
                if (!arg.startsWith("--")) {
                    throw new Exception(s"[arg] Invalid argument supplied as startup option: ${arg}. Skipping argument.")
                }

                val key = arg.drop(2)

                if (key.contains("=")) {
                    val parts = key.split("=")
                    if (parts.length != 2) {
                        throw new Exception(s"[arg] Invalid startup argument's value: ${arg}. Skipping argument.")
                    }

                    options += (parts(0) -> parts(1))
                }

                else {
                    options += (key -> true.toString())
                }
            }
        )

        options
    }
}
