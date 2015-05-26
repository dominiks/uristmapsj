/* Icon definitions */
var icons = {};
function get_icon(url) {
  if (icons[url] === undefined) {
    var icon = L.icon({
      "iconUrl" : url,
      //"iconSize": [35, 35],
      //"iconAnchor": [17, 17]
    });
    icons[url] = icon;
  }
  return icons[url];
}