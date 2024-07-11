# osmium tags-filter planet.osm.pbf "n/name=*" -o name-nodes.osm.pbf --overwrite

import osmium
import csv

class NameTagExtractor(osmium.SimpleHandler):
    def __init__(self):
        super(NameTagExtractor, self).__init__()
        self.names = []

    def node(self, n):
        if 'name' in n.tags:
            self.names.append(n.tags['name'])

# Initialize the handler and apply it to the filtered OSM file
extractor = NameTagExtractor()
extractor.apply_file("name-nodes.osm.pbf")

# Write the results to a CSV file
with open("names.csv", "w", newline='') as csvfile:
    writer = csv.writer(csvfile)
    for name in extractor.names:
        writer.writerow([name])