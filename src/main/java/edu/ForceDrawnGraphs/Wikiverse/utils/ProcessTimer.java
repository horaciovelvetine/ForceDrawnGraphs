package edu.ForceDrawnGraphs.Wikiverse.utils;

public class ProcessTimer implements Loggable {
  private long startTime;
  private long splitElapsedTime;
  private long totalElapsedTime;
  private String processName;

  public ProcessTimer() {
    this.startTime = System.currentTimeMillis();
    this.splitElapsedTime = 0;
  }

  public ProcessTimer(String name) {
    this.startTime = System.currentTimeMillis();
    this.processName = name;
  }

  public void end() {
    this.totalElapsedTime = System.currentTimeMillis() - startTime;
    log(this.toString(), "process_timer.log");
    print(this.toString());
  }

  public void end(String name) {
    if (this.processName == null) {
      this.processName = name;
    } else {
      this.processName += name;
    }
    end();
  }

  public String getElapsedTime(String splitLabel) {
    long timeSinceStart = System.currentTimeMillis() - startTime;
    long timeSinceLast = timeSinceStart - splitElapsedTime;
    this.splitElapsedTime = timeSinceStart;
    return this.processName + ": " + splitLabel + " - " + timeSinceLast + "ms.";
  }

  @Override
  public String toString() {
    return "Finished: [" + processName + "] in " + totalElapsedTime + "ms.";
  }
}
