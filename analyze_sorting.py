import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os

# 1. Load the data
# If your file is named differently, change 'sorting_results.csv' here
CSV_FILE = 'sorting_results.csv'
df = pd.read_csv(CSV_FILE)

# Clean column names (remove any accidental spaces)
df.columns = df.columns.str.strip()

# Create output directory
os.makedirs("growth_charts", exist_ok=True)

# Define our two groups
n_log_n_group = ["Merge Sort", "Heap Sort", "Quick Sort"]
n_squared_group = ["Selection Sort", "Insertion Sort", "Bubble Sort"]

sns.set_theme(style="whitegrid")


def create_growth_chart(algo_list, title, filename, color_palette):
    plt.figure(figsize=(10, 6))

    # Filter data for only the algorithms in this group
    plot_df = df[df['Algorithm'].isin(algo_list)]

    # Sort by Array Size to ensure lines connect correctly
    plot_df = plot_df.sort_values(by="Array Size")

    # Create the line plot
    ax = sns.lineplot(
        data=plot_df,
        x="Array Size",
        y="Avg (ms)",
        hue="Algorithm",
        marker='o',
        markersize=8,
        linewidth=2.5,
        palette=color_palette
    )

    plt.title(title, fontsize=14, fontweight='bold', pad=15)
    plt.xlabel("Array Size (N)", fontsize=11)
    plt.ylabel("Average Execution Time (ms)", fontsize=11)

    # Add grid lines for easier reading
    plt.grid(True, which="both", ls="--", alpha=0.5)

    # Label the actual values on the points
    for _, row in plot_df.iterrows():
        plt.text(row['Array Size'], row['Avg (ms)'], f" {row['Avg (ms)']:.3f}",
                 verticalalignment='bottom', fontsize=9)

    plt.tight_layout()
    plt.savefig(f"growth_charts/{filename}", dpi=150)
    print(f"Chart saved: growth_charts/{filename}")
    plt.close()


# --- Generate the Charts ---

# Chart 1: The fast algorithms (N log N)
create_growth_chart(
    n_log_n_group,
    "Efficiency of O(n log n) Sorting Algorithms",
    "n_log_n_growth.png",
    "viridis"
)

# Chart 2: The classic algorithms (N^2)
create_growth_chart(
    n_squared_group,
    "Efficiency of O(n²) Sorting Algorithms",
    "n_squared_growth.png",
    "flare"
)

print("\nProcessing complete. Check the 'growth_charts' folder for your results.")