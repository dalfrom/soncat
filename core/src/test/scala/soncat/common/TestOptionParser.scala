package soncat.common

import munit.FunSuite


class TestOptionParser extends munit.FunSuite {
    test("parseOptions") {
        val args = Array("--port=8080", "--host=localhost", "--debug")
        val options = OptionParser.parseOptions(args)

        assert(options("port") == "8080")
        assert(options("host") == "localhost")
        assert(options("debug") == "true")
    }
}