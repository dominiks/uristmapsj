package org.uristmaps.tasks;

/**
 * This meta task depends on all separate tasks that generate the output required for a full map build.
 */
public class FullBuildMetaTask extends Task {
    @Override
    public void work() {
        // I don't do anything.
    }

    @Override
    public String[] getDependantTasks() {
        return new String[] {
                "TilesetTask", "BmpConvertTask", "BiomeRenderer", "SitesGeojson",
                "CompileUristJs", "CompileIndex", "DistResources"
        };
    }

    @Override
    public String getName() {
        return "FullBuild";
    }
}
