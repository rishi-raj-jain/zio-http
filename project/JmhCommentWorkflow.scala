import BuildHelper.Scala213
import sbtghactions.GenerativePlugin.autoImport.{UseRef, WorkflowJob, WorkflowStep}

object JmhCommentWorkflow {
  def jmhBenchmark() = Seq(
    WorkflowJob(
      runsOnExtraLabels = List("zio-http"),
      id = "comment_jmh_current",
      name = "comment_jmh_current",
      oses = List("centos"),
      scalas = List(Scala213),
      needs = List("run_Jmh_current_BenchMark"),
      steps = List(
        WorkflowStep.Use(
          ref = UseRef.Public("actions", "download-artifact", "v3"),
          Map(
            "name" -> "jmh_result"
          )
        ),
        WorkflowStep.Run(
          commands = List("""bash <(value=`cat HttpCollectEval.txt`
                          |echo ::set-output name=result::$value)""".stripMargin),
          id = Some("echo_value"),
          name = Some("echo_value")
        ),
        WorkflowStep.Use(
          ref = UseRef.Public("peter-evans", "commit-comment", "v1"),
          params = Map(
            "sha"  -> "${{github.sha}}",
            "body" ->
              """
                 |**\uD83D\uDE80 Jmh Benchmark:**
                 |
                 |- Current Branch:
                 |${{steps.echo_value.outputs.result}}""".stripMargin,
          ),
        ),
      ),
    ),

  )

  def apply(): Seq[WorkflowJob] = jmhBenchmark()
}