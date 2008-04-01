# -----------------------------------------------------------------------------
#
#                                               The JLibs Buildr Configuration
#                                                                   by lhunath
# 
#
# Group identifier for your projects
GROUP = "com.lyndir.lhunath.lib"
COPYRIGHT = "Lhunath (C) 2007-2008"
# 
# Versioning
VERSION_NUMBER = "1.0.0"
NEXT_VERSION = "1.0.1"
# 
# Repositories and artifacts
require "../JLibs/artifacts"



#
# Project Definition
desc "Lyndir Jlibs:  Java Convenience API"
define "JLibs" do

  project.version = VERSION_NUMBER
  project.group = GROUP
  manifest["Implementation-Vendor"] = COPYRIGHT

  desc "JLibs Crypto: Cryptography API"
  define "crypto" do
    compile.with project("system"), BOUNCYCASTLE

    package :jar
  end

  desc "JLibs Data: Data Management API"
  define "data" do
    compile.with project("system"), JTIDY, HTTPCLIENT

    package :jar
  end

  desc "JLibs Geo: User Interface API"
  define "geo" do
    compile.with projects("math", "system"), JGOODIES.looks

    package :jar
  end

  desc "JLibs Math: Mathematics API"
  define "math" do
    compile.with project("system")

    package :jar
  end

  desc "JLibs Piccolo: Piccolo ZUI API"
  define "piccolo" do
    compile.with projects("math", "system"),
        artifact("piccolo:piccolo:jar:1.2").from(file("lib/piccolo-1.2.jar")),
        artifact("piccolo:piccolox:jar:1.2").from(file("lib/piccolox-1.2.jar"))

    package :jar
  end

  desc "JLibs Pigeon: Networking API"
  define "pigeon" do
    compile.with project("system"), XOM

    package :jar
  end

  desc "JLibs Shade: The Shade Swing Application Template API"
  define "shade" do
    compile.with projects("geo", "math", "system"), JGOODIES.forms, JAVADESKTOP.timing, JAVADESKTOP.animatedtransitions

    package :jar
  end

  desc "JLibs System: Base Utilities and Logging API"
  define "system" do
    package :jar
  end

end
