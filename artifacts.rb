# -----------------------------------------------------------------------------
#
#                                               Shared Artifacts Configuration
#                                                                   by lhunath
# 
#
# Remote Artifact Repositories
repositories.remote << "http://repo1.maven.org/maven2/"
repositories.remote << "http://download.java.net/maven/2/"
repositories.remote << "http://www.ibiblio.org/maven2/"
# 
# Artifact Definitions
BOUNCYCASTLE    = [ "bouncycastle:bcpg-jdk15:jar:138", "bouncycastle:bcprov-jdk15:jar:138" ]
JTIDY           = "org.hibernate:jtidy-r8:jar:20060801"
HTTPCLIENT      = "commons-httpclient:commons-httpclient:jar:3.1"
JGOODIES        = struct(
    :animation  => "jgoodies:animation:jar:1.2.0",
    :binding    => "jgoodies:binding:jar:2.0.2",
    :forms      => "jgoodies:forms:jar:1.2.0",
    :looks      => "jgoodies:looks:jar:2.1.4",
    :validation => "jgoodies:validation:jar:2.0.0"
)
XOM             = "xom:xom:jar:1.1"
XPP             = "xpp3:xpp3_min:jar:1.1.3.4.O"
XSTREAM         = "com.thoughtworks.xstream:xstream:jar:1.2.2"
JAVADESKTOP     = struct(
    :timing     => "net.java.dev.timingframework:timingframework:jar:1.0",
    :animatedtransitions    => artifact("net.java.dev.animatedtransitions:animatedtransitions:jar:0.11").
                                    from(file("../JLibs/shade/lib/AnimatedTransitions-0.11.jar"))
)
JUNG            = struct(
    :threeD     => "jung:jung-3d:jar:2.0-alpha2",
    :algorithms => "jung:jung-algorithms:jar:2.0-alpha2",
    :api        => "jung:jung-api:jar:2.0-alpha2",
    :graphImpl  => "jung:jung-graph-impl:jar:2.0-alpha2",
    :io         => "jung:jung-io:jar:2.0-alpha2",
    :jai        => "jung:jung-jai:jar:2.0-alpha2",
    :prefuse    => "jung:jung-prefuse:jar:2.0-alpha2",
    :swtRendering           => "jung:jung-swt-rendering:jar:2.0-alpha2",
    :swtVisualization       => "jung:jung-swt-visualization:jar:2.0-alpha2",
    :visualization          => "jung:jung-visualization:jar:2.0-alpha2"
)
COLLECTIONS     = "net.sourceforge.collections:collections-generic:jar:4.01"


