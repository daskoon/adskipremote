
import plotly.graph_objects as go

# Create figure
fig = go.Figure()

# Define node positions (x, y) for vertical flow
nodes = [
    {"label": "User Presses<br>SKIP AD Button<br>on Watch", "x": 0.5, "y": 9, "color": "#ADD8E6"},
    {"label": "Watch App<br>MessageClient<br>sendMessage()", "x": 0.5, "y": 8, "color": "#ADD8E6"},
    {"label": "Wear OS<br>Connection<br>(BT/WiFi)", "x": 0.5, "y": 7, "color": "#9370DB"},
    {"label": "Phone Receives<br>Message via<br>MessageClient", "x": 0.5, "y": 6, "color": "#90EE90"},
    {"label": "Accessibility<br>Service<br>Activated", "x": 0.5, "y": 5, "color": "#90EE90"},
    {"label": "Search YouTube<br>UI for Skip Ad<br>Button", "x": 0.5, "y": 4, "color": "#90EE90"},
    {"label": "Found Button?<br>Check if<br>Clickable", "x": 0.5, "y": 3, "color": "#FFEB8A"},
    {"label": "Perform<br>ACTION_CLICK<br>on Button", "x": 0.5, "y": 2, "color": "#90EE90"},
    {"label": "Ad Skipped!<br>Success", "x": 0.5, "y": 1, "color": "#FFD700"}
]

# Add arrows between nodes
for i in range(len(nodes) - 1):
    fig.add_annotation(
        x=nodes[i+1]["x"], y=nodes[i+1]["y"] + 0.35,
        ax=nodes[i]["x"], ay=nodes[i]["y"] - 0.35,
        xref="x", yref="y",
        axref="x", ayref="y",
        showarrow=True,
        arrowhead=2,
        arrowsize=1.5,
        arrowwidth=2,
        arrowcolor="#333333"
    )

# Add node rectangles and labels
for node in nodes:
    # Add rectangle
    fig.add_shape(
        type="rect",
        x0=node["x"] - 0.15, y0=node["y"] - 0.35,
        x1=node["x"] + 0.15, y1=node["y"] + 0.35,
        line=dict(color="#333333", width=2),
        fillcolor=node["color"]
    )
    
    # Add text
    fig.add_annotation(
        x=node["x"], y=node["y"],
        text=node["label"],
        showarrow=False,
        font=dict(size=11, color="#13343B"),
        align="center"
    )

# Update layout
fig.update_layout(
    title="YouTube Ad Skip Flow",
    xaxis=dict(
        showgrid=False,
        showticklabels=False,
        zeroline=False,
        range=[0, 1]
    ),
    yaxis=dict(
        showgrid=False,
        showticklabels=False,
        zeroline=False,
        range=[0, 10]
    ),
    showlegend=False,
    plot_bgcolor="white"
)

# Save the figure
fig.write_image("flowchart.png")
fig.write_image("flowchart.svg", format="svg")

print("Flowchart saved successfully!")
